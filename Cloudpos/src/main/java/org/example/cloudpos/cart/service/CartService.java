package org.example.cloudpos.cart.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.cart.exception.CartExpiredException;
import org.example.cloudpos.cart.fsm.CartEvent;
import org.example.cloudpos.product.dto.ProductSummaryDto;
import org.example.cloudpos.product.service.ProductService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CartService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ProductService productService;
    private static final Duration TTL=Duration.ofMinutes(5);

    private String itemsKey(String cartId) { return "cart:" + cartId + ":items"; }          // List: [productId,...]
    private String itemSetKey(String cartId) { return "cart:" + cartId + ":itemset"; }
    private String qtyKey(String cartId, String productId) { return "cart:" + cartId + ":qty:" + productId; } // String: "3"
    private String stateKey(String cartId) { return "cart:" + cartId + ":state"; }

    private static final EnumMap<CartState, EnumMap<CartEvent, CartState>> TRANSITIONS = new EnumMap<>(CartState.class);
    static {
        // EMPTY
        var empty = new EnumMap<CartEvent, CartState>(CartEvent.class);
        empty.put(CartEvent.ADD_ITEM, CartState.IN_PROGRESS);
        TRANSITIONS.put(CartState.EMPTY, empty);

        // IN_PROGRESS
        var inProgress = new EnumMap<CartEvent, CartState>(CartEvent.class);
        inProgress.put(CartEvent.ADD_ITEM, CartState.IN_PROGRESS);
        inProgress.put(CartEvent.REMOVE_ITEM, CartState.IN_PROGRESS); // 실수량 0 → EMPTY는 후처리
        inProgress.put(CartEvent.CHECKOUT, CartState.CHECKOUT_PENDING);
        TRANSITIONS.put(CartState.IN_PROGRESS, inProgress);

        // CHECKOUT_PENDING
        var checkout = new EnumMap<CartEvent, CartState>(CartEvent.class);
        checkout.put(CartEvent.PAYMENT_SUCCESS, CartState.CLOSED);
        checkout.put(CartEvent.CANCEL, CartState.IN_PROGRESS);
        TRANSITIONS.put(CartState.CHECKOUT_PENDING, checkout);

        // CLOSED(종단)
        TRANSITIONS.put(CartState.CLOSED, new EnumMap<>(CartEvent.class));
    }

    private Optional<CartState> nextStateOpt(CartState cur, CartEvent event) {
        var byEvent = TRANSITIONS.get(cur);
        if (byEvent == null) return Optional.empty();
        return Optional.ofNullable(byEvent.get(event));
    }

    public boolean createCart(String cartId) {
        redisTemplate.opsForValue().setIfAbsent(stateKey(cartId), CartState.EMPTY.name(), TTL);
        return true;
    }

    public CartState getState(String cartId){
        String s = redisTemplate.opsForValue().get(stateKey(cartId));
        return (s == null) ? CartState.EMPTY : CartState.valueOf(s);
    }


    public boolean addFirstTime(String cartId, String productId) {
        ensureAlive(cartId);
        var s = getState(cartId);
        if (s == CartState.CHECKOUT_PENDING || s == CartState.CLOSED) {
            throw new IllegalStateException("현재 상태에서는 장바구니를 수정할 수 없습니다: " + s);
        }
        Long added = redisTemplate.opsForSet().add(itemSetKey(cartId), productId);
        if(added != null && added == 1L){
            redisTemplate.opsForList().rightPush(itemsKey(cartId), productId);
            redisTemplate.opsForValue().setIfAbsent(qtyKey(cartId, productId),"1",TTL);
        }else{
            redisTemplate.opsForValue().increment(qtyKey(cartId, productId), 1);
        }

        transition(cartId, CartEvent.ADD_ITEM);
        redisTemplate.expire(qtyKey(cartId, productId), TTL);
        return true;
    }



    public boolean addOne(String cartId, String productId) {

        ensureAlive(cartId);

        var s = getState(cartId);
        if (s == CartState.CHECKOUT_PENDING || s == CartState.CLOSED) {
            throw new IllegalStateException("현재 상태에서는 장바구니를 수정할 수 없습니다: " + s);
        }

        redisTemplate.opsForValue().increment(qtyKey(cartId, productId));

        transition(cartId, CartEvent.ADD_ITEM);

        redisTemplate.expire(qtyKey(cartId, productId), TTL);
        return true;
    }

    public boolean removeOne(String cartId, String productId) {
        ensureAlive(cartId);

        var s = getState(cartId);
        if (s == CartState.CHECKOUT_PENDING || s == CartState.CLOSED) {
            throw new IllegalStateException("현재 상태에서는 장바구니를 수정할 수 없습니다: " + s);
        }

        int cur=getQuantity(cartId, productId);
        if(cur<=1) return false;
        redisTemplate.opsForValue().decrement(qtyKey(cartId, productId));
        transition(cartId, CartEvent.REMOVE_ITEM);
        redisTemplate.expire(qtyKey(cartId, productId), TTL);
        return true;
    }

    public boolean removeItem(String cartId, String productId) {
        ensureAlive(cartId);
        var s = getState(cartId);
        if (s == CartState.CHECKOUT_PENDING || s == CartState.CLOSED) {
            throw new IllegalStateException("현재 상태에서는 장바구니를 수정할 수 없습니다: " + s);
        }

        redisTemplate.delete(qtyKey(cartId, productId));
        redisTemplate.opsForSet().remove(itemSetKey(cartId), productId);
        redisTemplate.opsForList().remove(itemsKey(cartId),0,productId);
        transition(cartId, CartEvent.REMOVE_ITEM);
        return true;
    }

    public List<CartItemDto> getAll(String cartId) {
        ensureAlive(cartId);

        List<String> ids = redisTemplate.opsForList().range(itemsKey(cartId), 0, -1);
        if (ids == null || ids.isEmpty()) return List.of();

        List<String> qtyKeys = ids.stream().map(pid -> qtyKey(cartId, pid)).toList();
        List<String> quantities = redisTemplate.opsForValue().multiGet(qtyKeys);

        List<CartItemDto> result = new ArrayList<>(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            String pid = ids.get(i);
            String qStr = (quantities == null) ? null : quantities.get(i);
            int qty = (qStr == null) ? 0 : Integer.parseInt(qStr);
            if (qty < 1) continue;

            ProductSummaryDto p = productService.findSummaryByProductId(pid);
            if (p == null) {
                continue;
            }
            result.add(new CartItemDto(p, qty));
        }
        return result;
    }

    public void beginCheckout(String cartId) {
        ensureAlive(cartId);
        List<String> ids=redisTemplate.opsForList().range(itemsKey(cartId), 0, -1);
        if(ids == null || ids.isEmpty()) {
            throw new IllegalStateException("빈 장바구니는 결제를 시작 할 수 없음");
        }
        transition(cartId, CartEvent.CHECKOUT);

    }

    public void paymentSuccess(String cartId) {
        ensureAlive(cartId);
        if (getState(cartId) != CartState.CHECKOUT_PENDING) {
            throw new IllegalStateException("결제 성공은 CHECKOUT_PENDING에서만 가능합니다.");
        }
        transition(cartId, CartEvent.PAYMENT_SUCCESS);
        clear(cartId);

    }

    public void cancelCheckout(String cartId) {
        ensureAlive(cartId);
        if(getState(cartId) != CartState.CHECKOUT_PENDING) {
            throw new IllegalStateException("결제 취소는 CHECKOUT_PENDING에서만 가능합니다.");
        }
        transition(cartId, CartEvent.CANCEL);
    }

    public void clear(String cartId) {
        Set<String> pids = redisTemplate.opsForSet().members(itemSetKey(cartId));

        if (pids != null && !pids.isEmpty()) {
            List<String> qtyKeys = pids.stream()
                    .map(pid -> qtyKey(cartId, pid))
                    .toList();

            redisTemplate.delete(qtyKeys);
        }
        redisTemplate.delete(itemSetKey(cartId));
        redisTemplate.delete(itemsKey(cartId));
        redisTemplate.delete(stateKey(cartId));
    }


    private void ensureAlive(String cartId) {
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(stateKey(cartId)))) {
            // stateKey가 없으면 세션 만료로 간주 → 잔여 키 정리
            clear(cartId);
            throw new CartExpiredException(cartId); // 404/410 등으로 매핑
        }
    }


    //상태전이, ttl 동기화?
    private void transition(String cartId, CartEvent event) {
        CartState cur = getState(cartId);
        CartState next = nextStateOpt(cur, event).orElse(cur);
        redisTemplate.opsForValue().set(stateKey(cartId), next.name(), TTL);
        redisTemplate.expire(itemsKey(cartId), TTL);
        redisTemplate.expire(itemSetKey(cartId), TTL);
    }

    private int getQuantity(String cartId, String productId) {
        String v=redisTemplate.opsForValue().get(qtyKey(cartId, productId));
        return (v == null) ? 0 : Integer.parseInt(v);
    }

}
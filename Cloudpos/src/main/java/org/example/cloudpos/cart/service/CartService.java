package org.example.cloudpos.cart.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.api.ProductSummaryHandlerApi;
import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.cart.dto.ProductSummary;
import org.example.cloudpos.cart.exception.CartExpiredException;
import org.example.cloudpos.cart.fsm.CartEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
/**
 * <h2>CartService</h2>
 *
 * 장바구니(Cart) 도메인의 핵심 애플리케이션 서비스입니다.
 *
 * <p>Redis를 기반으로 장바구니의 아이템 목록, 수량, 상태를 관리하며
 * 간단한 유한상태머신(FSM)을 통해 상태 전이를 처리합니다.
 * TTL(Time To Live)을 사용하여 비활성 장바구니는 일정 시간이 지나면 자동 만료됩니다.</p>
 *
 * <h3>주요 역할</h3>
 * <ul>
 *   <li>장바구니 생성 및 상태 관리 ({@link CartState})</li>
 *   <li>상품 추가·삭제 및 수량 조정</li>
 *   <li>결제 프로세스 전이: 결제 시작 / 성공 / 취소</li>
 *   <li>상품 요약 정보 조회: {@link ProductSummaryHandlerApi} 연동</li>
 *   <li>만료된 장바구니 접근 시 {@link CartExpiredException} 발생</li>
 * </ul>
 *
 * <h3>상태 전이 개요</h3>
 * <pre>
 * EMPTY → IN_PROGRESS → CHECKOUT_PENDING → CLOSED
 * </pre>
 *
 * <h3>TTL 관리</h3>
 * <p>각 Redis 키(cart:{id}:state/items/itemset/qty:productId)는
 * 쓰기 연산 시마다 TTL이 갱신되어, 사용자 활동이 있을 때마다 만료 시점이 연장됩니다.</p>
 *
 * <h3>예외 처리</h3>
 * <ul>
 *   <li>{@link CartExpiredException} – 만료된 장바구니 접근 시</li>
 *   <li>{@link IllegalStateException} – 허용되지 않은 상태 전이 또는 빈 장바구니 결제 시</li>
 * </ul>
 */


@Service
@RequiredArgsConstructor
public class CartService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ProductSummaryHandlerApi productSummaryHandlerApi;
    private static final Duration TTL=Duration.ofMinutes(5);

    private String itemsKey(String cartId) { return "cart:" + cartId + ":items"; }
    private String itemSetKey(String cartId) { return "cart:" + cartId + ":itemset"; }
    private String qtyKey(String cartId, String productId) { return "cart:" + cartId + ":qty:" + productId; }
    private String stateKey(String cartId) { return "cart:" + cartId + ":state"; }

    private static final EnumMap<CartState, EnumMap<CartEvent, CartState>> TRANSITIONS = new EnumMap<>(CartState.class);
    static {
        // EMPTY

        EnumMap<CartEvent, CartState> empty = new EnumMap<>(CartEvent.class);
        empty.put(CartEvent.ADD_ITEM, CartState.IN_PROGRESS);
        TRANSITIONS.put(CartState.EMPTY, empty);

        // IN_PROGRESS
        EnumMap<CartEvent, CartState> inProgress = new EnumMap<>(CartEvent.class);
        inProgress.put(CartEvent.ADD_ITEM, CartState.IN_PROGRESS);
        inProgress.put(CartEvent.REMOVE_ITEM, CartState.IN_PROGRESS);
        inProgress.put(CartEvent.CHECKOUT, CartState.CHECKOUT_PENDING);
        TRANSITIONS.put(CartState.IN_PROGRESS, inProgress);

        // CHECKOUT_PENDING
        EnumMap<CartEvent, CartState> checkout = new EnumMap<>(CartEvent.class);
        checkout.put(CartEvent.PAYMENT_SUCCESS, CartState.CLOSED);
        checkout.put(CartEvent.CANCEL, CartState.IN_PROGRESS);
        TRANSITIONS.put(CartState.CHECKOUT_PENDING, checkout);

        // CLOSED(종단)
        TRANSITIONS.put(CartState.CLOSED, new EnumMap<>(CartEvent.class));
    }


    private Optional<CartState> nextStateOpt(CartState cur, CartEvent event) {
        EnumMap<CartEvent, CartState> byEvent = TRANSITIONS.get(cur);
        if (byEvent == null) return Optional.empty();
        return Optional.ofNullable(byEvent.get(event));
    }

    private void refreshTtl(String cartId, String productIdOrNull) {
        redisTemplate.expire(stateKey(cartId), TTL);
        redisTemplate.expire(itemsKey(cartId), TTL);
        redisTemplate.expire(itemSetKey(cartId), TTL);
        if(productIdOrNull != null){
            redisTemplate.expire(qtyKey(cartId, productIdOrNull), TTL);
        }
    }

    private void refreshTtl(String cartId) {
        refreshTtl(cartId, null);
    }

    private void requireMutableCart(String cartId){
        ensureAlive(cartId);
        CartState state = getState(cartId);
        if(state == CartState.CHECKOUT_PENDING || state == CartState.CLOSED){
            throw new IllegalStateException("현재 상태에서는 장바구니를 수정할 수 없습니다: " + state);
        }
    }

    private void requireCheckoutPending(String cartId, String action){
        ensureAlive(cartId);
        CartState state = getState(cartId);
        if(state != CartState.CHECKOUT_PENDING){
            throw new IllegalStateException(action+"은(는) CHECKOUT_PENDING에서만 가능합니다.");
        }
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
        requireMutableCart(cartId);

        Long added = redisTemplate.opsForSet().add(itemSetKey(cartId), productId);
        if(added != null && added == 1L){
            redisTemplate.opsForList().rightPush(itemsKey(cartId), productId);
            redisTemplate.opsForValue().setIfAbsent(qtyKey(cartId, productId),"1",TTL);
        }else{
            redisTemplate.opsForValue().increment(qtyKey(cartId, productId), 1);
        }

        transition(cartId, CartEvent.ADD_ITEM);
        refreshTtl(cartId, productId);

        return true;
    }



    public boolean addOne(String cartId, String productId) {
        requireMutableCart(cartId);

        redisTemplate.opsForValue().increment(qtyKey(cartId, productId));

        transition(cartId, CartEvent.ADD_ITEM);

        refreshTtl(cartId, productId);

        return true;
    }

    public boolean removeOne(String cartId, String productId) {

        requireMutableCart(cartId);

        int cur=getQuantity(cartId, productId);
        if(cur<=1) return false;

        redisTemplate.opsForValue().decrement(qtyKey(cartId, productId));

        transition(cartId, CartEvent.REMOVE_ITEM);

        refreshTtl(cartId, productId);

        return true;
    }

    public boolean removeItem(String cartId, String productId) {
        requireMutableCart(cartId);

        redisTemplate.delete(qtyKey(cartId, productId));
        redisTemplate.opsForSet().remove(itemSetKey(cartId), productId);
        redisTemplate.opsForList().remove(itemsKey(cartId),0,productId);

        transition(cartId, CartEvent.REMOVE_ITEM);

        refreshTtl(cartId);

        return true;
    }

    public List<CartItemDto> getAll(String cartId) {
        ensureAlive(cartId);

        //productid
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

            ProductSummary p = productSummaryHandlerApi.getProductSummary(pid);
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
        refreshTtl(cartId);


    }

    public void paymentSuccess(String cartId) {

        requireCheckoutPending(cartId, "결제 성공" );

        transition(cartId, CartEvent.PAYMENT_SUCCESS);

        clear(cartId);

    }

    public void cancelCheckout(String cartId) {

        requireCheckoutPending(cartId, "결제 취소" );


        transition(cartId, CartEvent.CANCEL);
        refreshTtl(cartId);

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
            clear(cartId);
            throw new CartExpiredException(cartId); // 404/410 등으로 매핑
        }
    }


    //상태전이, ttl 동기화?
    private void transition(String cartId, CartEvent event) {
        CartState cur = getState(cartId);
        CartState next = nextStateOpt(cur, event).orElse(cur);
        redisTemplate.opsForValue().set(stateKey(cartId), next.name());

    }

    public int getQuantity(String cartId, String productId) {
        String v=redisTemplate.opsForValue().get(qtyKey(cartId, productId));
        return (v == null) ? 0 : Integer.parseInt(v);
    }

}
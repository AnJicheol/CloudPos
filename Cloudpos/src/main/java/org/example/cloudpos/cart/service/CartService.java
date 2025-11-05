package org.example.cloudpos.cart.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.cart.dto.ProductCartDto;
import org.example.cloudpos.cart.exception.CartExpiredException;
import org.example.cloudpos.cart.fsm.CartEvent;
import org.example.cloudpos.cart.fsm.CartStateMachine;
//import org.example.cloudpos.product.dto.ProductSummaryDto;
import org.example.cloudpos.product.service.ProductService;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CartService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ProductService productService;
    private static final Duration TTL=Duration.ofMinutes(5);

//    private ProductSummaryDto getProductInfo(String productId) {
//        // 실제 product 서비스에서 상품 정보를 가져오기
//        return productService.findSummaryByProductId(productId);
//    }

    private String itemsKey(String cartId) { return "cart:" + cartId + ":items"; }          // List: [productId,...]
    private String itemSetKey(String cartId) { return "cart:" + cartId + ":itemset"; }
    private String qtyKey(String cartId, String productId) { return "cart:" + cartId + ":qty:" + productId; } // String: "3"
    private String stateKey(String cartId) { return "cart:" + cartId + ":state"; }


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
        createCart(cartId);

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
        redisTemplate.opsForValue().increment(qtyKey(cartId, productId));

        transition(cartId, CartEvent.ADD_ITEM);

        redisTemplate.expire(qtyKey(cartId, productId), TTL);
        return true;
    }

    public boolean removeOne(String cartId, String productId) {
        ensureAlive(cartId);
        int cur=getQuantity(cartId, productId);
        if(cur<=1) return false;
        redisTemplate.opsForValue().decrement(qtyKey(cartId, productId));
        transition(cartId, CartEvent.REMOVE_ITEM);
        redisTemplate.expire(qtyKey(cartId, productId), TTL);
        return true;
    }

    public boolean removeItem(String cartId, String productId) {
        ensureAlive(cartId);
        redisTemplate.delete(qtyKey(cartId, productId));
        redisTemplate.opsForSet().remove(itemSetKey(cartId), productId);
        redisTemplate.opsForList().remove(itemsKey(cartId),0,productId);
        transition(cartId, CartEvent.REMOVE_ITEM);
        return true;
    }

    public List<CartItemDto> getAll(String cartId) {
        ensureAlive(cartId);

        // 1) 장바구니 상품 id 순서
        List<String> ids = redisTemplate.opsForList().range(itemsKey(cartId), 0, -1);
        if (ids == null || ids.isEmpty()) return List.of();

        // 2) 수량 일괄 조회 (multiGet)
        List<String> qtyKeys = ids.stream().map(pid -> qtyKey(cartId, pid)).toList();
        List<String> quantities = redisTemplate.opsForValue().multiGet(qtyKeys);

        // 3) ✅ 상품 정보 배치 조회
        Map<String, ProductSummaryDto> productMap = productService.findCartViewByProductIds(new HashSet<>(ids));

        // 4) 순서 보존하여 묶기
        List<CartItemDto> result = new ArrayList<>(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            String pid = ids.get(i);
            String qStr = (quantities == null) ? null : quantities.get(i);
            int qty = (qStr == null) ? 0 : Integer.parseInt(qStr);
            if (qty < 1) continue;

            ProductCartDto p = productMap.get(pid);
            if (p == null) {
                // 정책: 상품이 삭제되었거나 비공개면 스킵 or placeholder
                continue; // 또는 placeholder DTO 채우기
            }
            result.add(new CartItemDto(p, qty));
        }
        return result;
    }

    public void clear(String cartId) {
        Set<String> pids = redisTemplate.opsForSet().members(itemSetKey(cartId));

        // 2) qty:* 일괄 삭제
        if (pids != null && !pids.isEmpty()) {
            List<String> qtyKeys = pids.stream()
                    .map(pid -> qtyKey(cartId, pid))
                    .toList(); // Java 16+ (Java 21이면 OK)

            // 여러 키 한 번에 삭제 (호출 1번)
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
        CartState cur=getState(cartId);
        CartState next=CartStateMachine.next(cur, event).orElse(cur);
        redisTemplate.opsForValue().set(stateKey(cartId), next.name(), TTL);
        redisTemplate.expire(itemsKey(cartId), TTL);
        redisTemplate.expire(itemSetKey(cartId), TTL);
    }

    private int getQuantity(String cartId, String productId) {
        String v=redisTemplate.opsForValue().get(qtyKey(cartId, productId));
        return (v == null) ? 0 : Integer.parseInt(v);
    }

}

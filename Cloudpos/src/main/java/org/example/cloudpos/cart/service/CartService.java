package org.example.cloudpos.cart.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.api.ProductSummaryHandlerApi;
import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.domain.UlidGenerator;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.cart.dto.CreateCartResponse;
import org.example.cloudpos.cart.dto.ProductSummary;
import org.example.cloudpos.cart.exception.CartExpiredException;
import org.example.cloudpos.cart.fsm.CartEvent;
import org.example.cloudpos.cart.fsm.CartStateMachine;
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
 * * <h3>TTL 관리</h3>
 *  * <p>각 Redis 키(cart:{id}:state/order/items)는
 *  * 쓰기 연산 시마다 TTL이 갱신되어, 사용자 활동이 있을 때마다 만료 시점이 연장됩니다.</p>
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

    private String itemsHashKey(String cartId) { return "cart:" + cartId + ":items"; }
    private String itemSetKey(String cartId) { return "cart:" + cartId + ":itemset"; }
    private String stateKey(String cartId) { return "cart:" + cartId + ":state"; }



    private void refreshTtl(String cartId) {
        redisTemplate.expire(stateKey(cartId), TTL);
        redisTemplate.expire(itemsHashKey(cartId), TTL);
        redisTemplate.expire(itemSetKey(cartId), TTL);
    }


    private void requireMutableCart(String cartId){
        ensureAlive(cartId);
        CartState state = getState(cartId);
        if(state == CartState.CHECKOUT_PENDING || state == CartState.CLOSED){
            throw new IllegalStateException("현재 상태에서는 장바구니를 수정할 수 없습니다: " + state);
        }
    }

    /**
     * 주어진 cartId로 새로운 장바구니를 생성한다.
     * 이미 존재하면 아무 작업도 하지 않는다.
     *
     * @return
     */
    public CreateCartResponse createCart() {
        String cartId = UlidGenerator.generate();

        redisTemplate.opsForValue().setIfAbsent(stateKey(cartId), CartState.EMPTY.name(), TTL);
        return new CreateCartResponse(cartId);
    }

    /**
     * 장바구니의 현재 상태를 조회한다.
     * 상태 키가 없으면 EMPTY를 반환한다.
     */
    public CartState getState(String cartId){
        String s = redisTemplate.opsForValue().get(stateKey(cartId));
        return (s == null) ? CartState.EMPTY : CartState.valueOf(s);
    }

    /**
     * 장바구니에 상품을 처음 추가할 때 호출된다.
     * itemset/items/qty 초기 등록 및 TTL 갱신을 수행한다.
     */
    public void addFirstTime(String cartId, String productId) {
        requireMutableCart(cartId);

        boolean exists = Boolean.TRUE.equals(
                redisTemplate.opsForHash().hasKey(itemsHashKey(cartId), productId)
        );
        if(!exists){
            redisTemplate.opsForList().rightPush(itemSetKey(cartId), productId);
            redisTemplate.opsForHash().put(itemsHashKey(cartId), productId, "1");
        }else{
            redisTemplate.opsForHash().increment(itemsHashKey(cartId), productId, 1);
        }

        transition(cartId, CartEvent.ADD_ITEM);
        refreshTtl(cartId);

    }

    /**
     * 이미 담긴 상품의 수량을 delta 만큼 변경한다.
     * 수량이 1 미만이 되면 변경하지 않고 false를 반환한다.
     */
    public int changeQuantity(String cartId, String productId, int delta) {
        requireMutableCart(cartId);

        int cur=getQuantity(cartId, productId);
        int next=cur+delta;
        if(next<1) {
            throw new IllegalStateException("상품의 최소 수량은 1개입니다.");
        }

        redisTemplate.opsForHash().increment(itemsHashKey(cartId), productId, delta);

        CartEvent event = (delta > 0) ? CartEvent.ADD_ITEM : CartEvent.REMOVE_ITEM;
        transition(cartId, event);

        refreshTtl(cartId);

        return next;

    }

    /**
     * 장바구니에서 해당 상품을 완전히 제거한다.
     * qty/itemset/items 키를 모두 삭제한다.
     */

    public void removeItem(String cartId, String productId) {
        requireMutableCart(cartId);

        redisTemplate.opsForHash().delete(itemsHashKey(cartId), productId);
        redisTemplate.opsForList().remove(itemSetKey(cartId),0,productId);

        transition(cartId, CartEvent.REMOVE_ITEM);

        refreshTtl(cartId);

    }


    /**
     * 장바구니에 속한 모든 Redis 키를 삭제한다.
     * (state, itemHash, itemset)
     */
    public void clear(String cartId) {
        redisTemplate.delete(itemSetKey(cartId));
        redisTemplate.delete(itemsHashKey(cartId));
        redisTemplate.delete(stateKey(cartId));
    }

    /**
     * 장바구니의 모든 상품과 수량을 조회하여 DTO로 반환한다.
     * ProductSummaryHandlerApi를 통해 상품 정보를 조회한다.
     */
    public List<CartItemDto> getAll(String cartId) {
        ensureAlive(cartId);

        //productid
        List<String> ids = redisTemplate.opsForList().range(itemSetKey(cartId), 0, -1);
        if (ids == null || ids.isEmpty()) return List.of();

        List<Object> quantities = redisTemplate.opsForHash().multiGet(itemsHashKey(cartId), new ArrayList<>(ids));

        List<CartItemDto> result = new ArrayList<>(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            String pid = ids.get(i);
            Object qObj= (quantities == null) ? null : quantities.get(i);
            String qStr=(qObj == null) ? null : qObj.toString();
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

    private void ensureAlive(String cartId) {
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(stateKey(cartId)))) {
            clear(cartId);
            throw new CartExpiredException(cartId); // 404/410 등으로 매핑
        }
    }


    //상태전이, ttl 동기화?
    private void transition(String cartId, CartEvent event) {
        CartState cur = getState(cartId);
        CartState next = CartStateMachine.next(cur, event).orElse(cur);
        redisTemplate.opsForValue().set(stateKey(cartId), next.name());

    }

    public int getQuantity(String cartId, String productId) {
        Object v=redisTemplate.opsForHash().get(itemsHashKey(cartId), productId);
        if (v == null) return 0;

        String s = v.toString();
        if (s.isEmpty()) return 0;

        return Integer.parseInt(s);
    }

}
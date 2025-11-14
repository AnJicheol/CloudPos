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
 * 장바구니 도메인의 애플리케이션 서비스.
 *
 * <p><b>개요</b><br>
 * Redis를 사용해 장바구니의 아이템 목록·수량·상태를 관리하고, 간단한 유한상태머신(FSM)으로 상태 전이를 처리한다.
 * 모든 키에는 공통 TTL(기본 5분)을 적용하여 비활성 장바구니가 자동 만료되도록 한다.
 * 만료된 장바구니 접근 시 {@link org.example.cloudpos.cart.exception.CartExpiredException}을 던진다.
 * </p>
 *
 * <p><b>Redis 키 구조</b></p>
 * <ul>
 *   <li><code>cart:{cartId}:items</code> — <i>List</i>: 담긴 상품의 <code>productId</code>를 순서대로 저장 (중복 허용/표시용)</li>
 *   <li><code>cart:{cartId}:itemset</code> — <i>Set</i>: 담긴 상품의 <code>productId</code> 집합 (존재 여부/중복 제거용)</li>
 *   <li><code>cart:{cartId}:qty:{productId}</code> — <i>String</i>: 해당 상품의 수량(정수)</li>
 *   <li><code>cart:{cartId}:state</code> — <i>String</i>: 장바구니 상태({@link org.example.cloudpos.cart.domain.CartState})</li>
 * </ul>
 *
 * <p><b>상태 전이(FSM)</b></p>
 * <pre>
 * EMPTY --(ADD_ITEM)------------------&gt; IN_PROGRESS
 * IN_PROGRESS --(ADD_ITEM/REMOVE_ITEM)--> IN_PROGRESS   (* 실수량 0이면 후처리로 비울 수 있음)
 * IN_PROGRESS --(CHECKOUT)------------&gt; CHECKOUT_PENDING
 * CHECKOUT_PENDING --(PAYMENT_SUCCESS)-&gt; CLOSED
 * CHECKOUT_PENDING --(CANCEL)---------&gt; IN_PROGRESS
 * CLOSED --(종단)----------------------&gt; (더 이상 전이 없음)
 * </pre>
 *
 * <p><b>주요 동작</b></p>
 * <ul>
 *   <li>{@code createCart} — 초기 상태 키를 {@code EMPTY}로 생성</li>
 *   <li>{@code addFirstTime}/{@code addOne}/{@code removeOne}/{@code removeItem}
 *       — 아이템/수량 변경 및 관련 키 TTL 연장</li>
 *   <li>{@code getAll} — Redis의 상품 식별자와 수량을 조회하고,
 *       {@link org.example.cloudpos.cart.api.ProductSummaryHandlerApi}를 통해 상품 요약정보를 조회하여
 *       {@link org.example.cloudpos.cart.dto.CartItemDto} 리스트로 변환</li>
 *   <li>{@code beginCheckout}/{@code paymentSuccess}/{@code cancelCheckout}
 *       — 결제 프로세스 상태 전이</li>
 *   <li>{@code clear} — 아이템/수량/상태 관련 모든 키 삭제(결제 성공/만료 등)</li>
 * </ul>
 *
 * <p><b>예외 및 가드</b></p>
 * <ul>
 *   <li>세션 만료(상태 키 없음): {@link org.example.cloudpos.cart.exception.CartExpiredException}</li>
 *   <li>허용되지 않는 상태에서의 변경/전이:
 *       내부 헬퍼 {@code requireMutableCart}, {@code requireCheckoutPending}에서
 *       {@link IllegalStateException}을 던진다.</li>
 *   <li>{@code beginCheckout}는 빈 장바구니 금지: {@link IllegalStateException}</li>
 * </ul>
 *
 * <p><b>TTL/만료</b><br>
 * 쓰기 연산 시 {@link #transition(String, CartEvent)}와 수량 키에 대한 개별 {@code expire} 호출을 통해
 * 상태 키(<code>state</code>)와 아이템 목록 키(<code>items</code>, <code>itemset</code>), 수량 키(<code>qty:{productId}</code>)의
 * TTL을 갱신한다. 사용자 활동이 있을 때마다 장바구니의 만료 시점이 연장된다.
 * Redis 연산은 다중 키 트랜잭션이 아니므로(파이프라인/트랜잭션 미사용) 강한 원자성이 필요하다면
 * 별도의 트랜잭션/Lua 스크립트를 도입해야 한다.
 * </p>
 *
 * <p><b>스레드/일관성</b><br>
 * 본 구현은 단순성을 우선하며, 다중 요청 동시성에서 완전한 일관성을 보장하지 않는다.
 * 경쟁 조건을 최소화하려면 Lua 스크립트/Redis 트랜잭션/분산락 등을 검토한다.
 * </p>
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
        redisTemplate.expire(qtyKey(cartId, productId), TTL);
        return true;
    }



    public boolean addOne(String cartId, String productId) {
        requireMutableCart(cartId);

        redisTemplate.opsForValue().increment(qtyKey(cartId, productId));

        transition(cartId, CartEvent.ADD_ITEM);

        redisTemplate.expire(qtyKey(cartId, productId), TTL);
        return true;
    }

    public boolean removeOne(String cartId, String productId) {

        requireMutableCart(cartId);

        int cur=getQuantity(cartId, productId);
        if(cur<=1) return false;

        redisTemplate.opsForValue().decrement(qtyKey(cartId, productId));

        transition(cartId, CartEvent.REMOVE_ITEM);

        redisTemplate.expire(qtyKey(cartId, productId), TTL);
        return true;
    }

    public boolean removeItem(String cartId, String productId) {
        requireMutableCart(cartId);

        redisTemplate.delete(qtyKey(cartId, productId));
        redisTemplate.opsForSet().remove(itemSetKey(cartId), productId);
        redisTemplate.opsForList().remove(itemsKey(cartId),0,productId);
        transition(cartId, CartEvent.REMOVE_ITEM);
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

    }

    public void paymentSuccess(String cartId) {

        requireCheckoutPending(cartId, "결제 성공" );

        transition(cartId, CartEvent.PAYMENT_SUCCESS);

        clear(cartId);

    }

    public void cancelCheckout(String cartId) {

        requireCheckoutPending(cartId, "결제 취소" );


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

    public int getQuantity(String cartId, String productId) {
        String v=redisTemplate.opsForValue().get(qtyKey(cartId, productId));
        return (v == null) ? 0 : Integer.parseInt(v);
    }

}
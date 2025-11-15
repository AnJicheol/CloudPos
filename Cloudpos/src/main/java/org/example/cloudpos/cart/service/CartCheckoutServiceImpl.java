package org.example.cloudpos.cart.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.api.ProductSummaryHandlerApi;
import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.exception.CartExpiredException;
import org.example.cloudpos.cart.fsm.CartEvent;
import org.example.cloudpos.cart.fsm.CartStateMachine;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CartCheckoutServiceImpl {

    private String itemsHashKey(String cartId) { return "cart:" + cartId + ":items"; }
    private String itemSetKey(String cartId) { return "cart:" + cartId + ":itemset"; }
    private String stateKey(String cartId) { return "cart:" + cartId + ":state"; }


    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration TTL=Duration.ofMinutes(5);

    /**
     * 장바구니의 현재 상태를 조회한다.
     * 상태 키가 없으면 EMPTY를 반환한다.
     */
    public CartState getState(String cartId){
        String s = redisTemplate.opsForValue().get(stateKey(cartId));
        return (s == null) ? CartState.EMPTY : CartState.valueOf(s);
    }

    private void refreshTtl(String cartId) {
        redisTemplate.expire(stateKey(cartId), TTL);
        redisTemplate.expire(itemsHashKey(cartId), TTL);
        redisTemplate.expire(itemSetKey(cartId), TTL);
    }


    private void requireCheckoutPending(String cartId, String action){
        ensureAlive(cartId);
        CartState state = getState(cartId);
        if(state != CartState.CHECKOUT_PENDING){
            throw new IllegalStateException(action+"은(는) CHECKOUT_PENDING에서만 가능합니다.");
        }
    }

    /**
     * 결제를 시작한다.
     * 빈 장바구니일 경우 예외를 발생시킨다.
     */
    public void beginCheckout(String cartId) {

        ensureAlive(cartId);

        List<String> ids=redisTemplate.opsForList().range(itemSetKey(cartId), 0, -1);

        if(ids == null || ids.isEmpty()) {
            throw new IllegalStateException("빈 장바구니는 결제를 시작 할 수 없음");
        }

        transition(cartId, CartEvent.CHECKOUT);
        refreshTtl(cartId);


    }

    /**
     * 결제 성공 처리.
     * CHECKOUT_PENDING 상태에서만 호출 가능하며 장바구니 데이터를 삭제한다.
     */
    public void paymentSuccess(String cartId) {

        requireCheckoutPending(cartId, "결제 성공" );

        transition(cartId, CartEvent.PAYMENT_SUCCESS);

        clear(cartId);

    }

    /**
     * 결제 취소 처리.
     * CHECKOUT_PENDING 상태를 IN_PROGRESS로 되돌린다.
     */
    public void cancelCheckout(String cartId) {

        requireCheckoutPending(cartId, "결제 취소" );


        transition(cartId, CartEvent.CANCEL);
        refreshTtl(cartId);

    }




    private void ensureAlive(String cartId) {
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(stateKey(cartId)))) {
            clear(cartId);
            throw new CartExpiredException(cartId); // 404/410 등으로 매핑
        }
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

    //상태전이, ttl 동기화?
    private void transition(String cartId, CartEvent event) {
        CartState cur = getState(cartId);
        CartState next = CartStateMachine.next(cur, event).orElse(cur);
        redisTemplate.opsForValue().set(stateKey(cartId), next.name());

    }
}

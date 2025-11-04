package org.example.cloudpos.cart.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.fsm.CartEvent;
import org.example.cloudpos.cart.fsm.CartStateMachine;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration TTL=Duration.ofMinutes(5);

    private String key(String cartId) {
        return "cart:" + cartId;
    }

    private String stateKey(String cartId) {
        return "cart:" + cartId + ":state";
    }

    public boolean createCart(String cartId) {
        redisTemplate.opsForValue().set(stateKey(cartId), CartState.EMPTY.name(),TTL);
        return true;
    }

    public boolean addItem(String cartId, String productId, int quantity) {
        String redisKey = key(cartId);
        CartState currentState = CartState.EMPTY;
        CartState next=transition(currentState, CartEvent.ADD_ITEM);

        removeItem(cartId,productId);
        redisTemplate.opsForList().rightPush(redisKey,productId+":"+quantity);
        redisTemplate.opsForValue().set(stateKey(cartId), next.name(), TTL);
        redisTemplate.expire(redisKey, TTL);
        return true;
    }

    public boolean removeItem(String cartId, String productId) {
        String redisKey = key(cartId);
        List<String> items = redisTemplate.opsForList().range(redisKey, 0, -1);
        if(items==null || items.isEmpty()) return false;

        for(String)

    }


    private CartState transition(CartState currentState, CartEvent event) {
        Optional<CartState> next = CartStateMachine.next(currentState, event);
        return next.orElse(currentState);
    }

}

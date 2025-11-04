package org.example.cloudpos.cart.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.cart.dto.ProductCartDto;
import org.example.cloudpos.cart.fsm.CartEvent;
import org.example.cloudpos.cart.fsm.CartStateMachine;
import org.example.cloudpos.product.service.ProductService;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration TTL=Duration.ofMinutes(5);

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
        redisTemplate.opsForValue().increment(qtyKey(cartId, productId));

        transition(cartId, CartEvent.ADD_ITEM);

        redisTemplate.expire(qtyKey(cartId, productId), TTL);
        return true;
    }

    public boolean removeOne(String cartId, String productId) {
        int cur=getQuantity(cartId, productId);
        if(cur<=1) return false;
        redisTemplate.opsForValue().decrement(qtyKey(cartId, productId));
        transition(cartId, CartEvent.REMOVE_ITEM);
        redisTemplate.expire(qtyKey(cartId, productId), TTL);
        return true;
    }

    public boolean removeItem(String cartId, String productId) {
        redisTemplate.delete(qtyKey(cartId, productId));
        redisTemplate.opsForSet().remove(itemSetKey(cartId), productId);
        redisTemplate.opsForList().remove(itemsKey(cartId),0,productId);
        transition(cartId, CartEvent.REMOVE_ITEM);
        return true;
    }

    public List<CartItemDto> getAll(String cartId) {
        List<String> ids = redisTemplate.opsForList().range(itemsKey(cartId), 0, -1);
        if (ids == null || ids.isEmpty()) return List.of();

        // 1) qty 키들을 미리 만들어두고
        List<String> qtyKeys = ids.stream()
                .map(pid -> qtyKey(cartId, pid))
                .toList();

        // 2) 한번에 가져오기 (문자열로 바로 받음)
        List<String> quantities = redisTemplate.opsForValue().multiGet(qtyKeys);

        List<CartItemDto> result = new ArrayList<>(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            String pid = ids.get(i);
            String qStr = (quantities == null) ? null : quantities.get(i);
            int qty = (qStr == null) ? 0 : Integer.parseInt(qStr);
            if (qty < 1) continue;

            ProductCartDto p = getProductInfo(pid);
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

package org.example.cloudpos.cart.redis;

import java.util.Map;

public interface CartItemRepository {

    /** 상품 수량 증감 (+면 증가, -면 감소, 0/음수면 제거) */
    void increment(String cartId, String productId, int delta);

    /** 전체 아이템 조회: productId -> quantity */
    Map<String, Integer> getAll(String cartId);

    /** 특정 상품 제거 */
    void remove(String cartId, String productId);

    /** 장바구니 비우기 */
    void clear(String cartId);

    /** 장바구니 내 상품 종류 수 (distinct count) */
    long itemKinds(String cartId);

}

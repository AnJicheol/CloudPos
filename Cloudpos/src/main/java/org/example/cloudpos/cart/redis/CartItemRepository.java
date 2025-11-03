package org.example.cloudpos.cart.redis;

import java.util.Map;
/**
 * 장바구니 아이템 저장소 (Redis 기반)
 *
 * <p>
 * - Redis에서 {@code cartId} 별로 상품 목록을 관리한다.<br>
 * - 각 상품({@code productId})과 수량(quantity)을 Key-Value 형태로 저장한다.<br>
 * - 도메인 계층은 Redis 기술 세부사항에 의존하지 않고, 본 인터페이스만 의존한다.
 * </p>
 *
 * <p><b>역할</b></p>
 * <ul>
 *     <li>상품 수량 증감</li>
 *     <li>상품 단건/전체 조회 및 삭제</li>
 *     <li>장바구니 비우기</li>
 *     <li>상품 종류 수 조회</li>
 * </ul>
 *
 * <p>실제 구현체는 {@code RedisTemplate} 또는 {@code StringRedisTemplate} 기반으로 작성된다.</p>
 */
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

package org.example.cloudpos.product.dto;

import org.example.cloudpos.product.ProductStatus;

/**
 * 상품 수정(PATCH) 요청을 표현하는 DTO입니다.
 *
 * <p>요청 본문으로 전달되며, 모든 필드는 선택적(optional)입니다.
 * 즉, null이 아닌 값만 기존 상품 엔티티에 반영됩니다.</p>
 *
 * <p>예시 요청 JSON:</p>
 * <pre>{@code
 * {
 *   "name": "아이스 아메리카노",
 *   "price": 4000
 * }
 * }</pre>
 *
 * @param name   변경할 상품명 (선택)
 * @param price  변경할 상품 가격 (선택)
 * @param status 변경할 상품 상태 (선택, 예: ACTIVE, ARCHIVED)
 * @param imageUrl 변경할 대표 이미지 URL(선택)
 *
 * @author Esther
 * @since 1.0
 */
public record ProductUpdateRequest(
        String name,
        Integer price,
        ProductStatus status,
        String imageUrl
) {}

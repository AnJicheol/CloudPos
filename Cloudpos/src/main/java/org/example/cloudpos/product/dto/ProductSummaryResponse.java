package org.example.cloudpos.product.dto;

/**
 * 상품의 요약 정보를 표현하는 데이터 전송 객체(DTO)입니다.
 *
 * <p>상품 상세 정보 대신, {@code productId}, {@code name}, {@code price} 등
 * 핵심 필드만 포함하여 목록 조회나 장바구니 등에서 간략히 사용됩니다.</p>
 *
 * @param productId 상품 식별자 (비즈니스용 ID)
 * @param name 상품명
 * @param price 상품 가격
 * @since 1.0
 */
public record ProductSummaryResponse(
        String productId,
        String name,
        int price
) {}

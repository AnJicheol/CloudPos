package org.example.cloudpos.product.dto;

/**
 * 상품 요약 정보를 담는 DTO입니다.
 *
 * <p>상품 목록 조회 시, 핵심 정보(productId, name, price, imageUrl)만 반환할 때 사용됩니다.</p>
 */
public record ProductSummary(
        String productId,
        String name,
        int price
) {}

package org.example.cloudpos.product.dto;

import org.example.cloudpos.product.ProductStatus;

/**
 * 상품 조회 또는 생성 결과를 반환하는 응답 DTO입니다.
 *
 * <p>클라이언트 또는 다른 도메인(재고, 주문 등)이
 * 상품 정보를 확인할 때 사용되는 응답 형태입니다.</p>
 *
 * <p>엔티티 {@link org.example.cloudpos.product.Product}와 1:1로 매핑되지만,
 * API 계약을 보장하기 위해 엔티티를 직접 노출하지 않고 DTO로 응답합니다.</p>
 *
 * @param id        DB 기본 키 (자동 증가)
 * @param productId 비즈니스용 상품 코드 (외부 식별자)
 * @param name      상품명
 * @param price     가격(원 단위)
 * @param status    현재 상품 상태 (ACTIVE, INACTIVE, ARCHIVED 등)
 *
 * @author Esther
 * @since 1.0
 */
public record ProductResponse(
        Long id,
        String productId,
        String name,
        int price,
        ProductStatus status
) {}

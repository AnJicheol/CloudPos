package org.example.cloudpos.inventory.dto;

/**
 * 인벤토리(매장) 생성 결과 응답 DTO입니다.
 *
 * @param id 내부 PK
 * @param inventoryId 외부 식별자(ULID)
 * @param name 매장명
 * @param productId 참조 상품 ID
 * @param productName 참조 상품 이름
 */
public record InventoryResponse(
        Long id,
        String inventoryId,
        String name,
        Long productId,
        String productName
) {}

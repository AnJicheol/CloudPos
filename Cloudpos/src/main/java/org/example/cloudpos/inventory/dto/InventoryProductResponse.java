package org.example.cloudpos.inventory.dto;

import org.example.cloudpos.product.domain.Product;
import org.example.cloudpos.product.domain.ProductStatus;

/**
 * 매장이 보유한 상품 정보를 반환하는 DTO.
 */
public record InventoryProductResponse(
        Long productId,
        String name,
        int price,
        ProductStatus status,
        String imageUrl
) {
    public static InventoryProductResponse from(Product p) {
        return new InventoryProductResponse(
                p.getId(),
                p.getName(),
                p.getPrice(),
                p.getStatus(),
                p.getImageUrl()
        );
    }
}

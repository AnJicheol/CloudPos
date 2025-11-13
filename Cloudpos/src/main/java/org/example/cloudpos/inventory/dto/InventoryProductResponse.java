package org.example.cloudpos.inventory.dto;

import org.example.cloudpos.product.domain.Product;
import org.example.cloudpos.product.domain.ProductStatus;

/**
 * 매장(Inventory)에 등록된 상품 정보를 클라이언트에 반환하는 응답 DTO.
 *
 * <p>매장별 상품 목록 조회 시 사용되며,
 * 상품의 핵심 속성(상품 ID, 이름, 가격, 상태, 이미지 URL)을 제공합니다.</p>
 *
 * <p>엔티티 {@link Product}의 일부 필드를 노출하는 형태로,
 * 매장 단위 상품 리스트를 표현합니다.</p>
 *
 * <h2>예시 응답(JSON)</h2>
 * <pre>{@code
 * {
 *   "productId": "PRD_01HXXXXXX",
 *   "name": "아메리카노",
 *   "price": 3000,
 *   "status": "ACTIVE",
 *   "imageUrl": "https://example.com/images/americano.jpg"
 * }
 * }</pre>
 *
 * @param productId 상품 식별자(비즈니스 키, ULID 문자열 등)
 * @param name 상품명
 * @param price 상품 가격
 * @param status 상품 상태 ({@link ProductStatus})
 * @param imageUrl 상품 이미지 URL (선택)
 */
public record InventoryProductResponse(
        String productId,
        String name,
        int price,
        ProductStatus status,
        String imageUrl
)
 {
    /**
     * {@link Product} 엔티티를 {@code InventoryProductResponse}로 변환합니다.
     *
     * @param p 변환할 상품 엔티티
     * @return 상품 정보를 담은 응답 DTO
     */
    public static InventoryProductResponse from(Product p) {
        return new InventoryProductResponse(
                p.getProductId(),
                p.getName(),
                p.getPrice(),
                p.getStatus(),
                p.getImageUrl()
        );
    }
}

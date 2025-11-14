package org.example.cloudpos.inventory.listener;

import org.example.cloudpos.product.dto.ProductSummaryDto;

public interface InventoryListener {

    /**
     * 인벤토리에 productId로 상품 요약 정보를 요청한다.
     *
     * @param productId Product 식별자 (ULID)
     * @return ProductSummaryDto (Inventory → Cart 전달용)
     */
    ProductSummaryDto getProduct(String productId);
}

package org.example.cloudpos.inventory.listener;

import org.example.cloudpos.product.dto.ProductSummaryDto;

public interface ProductReplyListener {

    /** 상품이 존재하지 않으면 호출 */
    void onProductNotFound(String inventoryId, String productId);

    /** 상품 판매 가능 여부 응답 */
    void onProductSellable(String inventoryId, String productId, boolean sellable);

    /** 상품 요약 정보 응답 */
    void onProductSummary(String inventoryId, String productId, ProductSummaryDto summary);
}

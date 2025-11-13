package org.example.cloudpos.product.api;

import org.example.cloudpos.inventory.listener.ProductReplyListener;

public interface ProductAccessApi {

    /**
     * 인벤토리가 상품을 진열하기 위해
     * "이 상품 팔아도 되나?" 를 확인하는 콜백형 API.
     */
    void requestSellableCheck(String inventoryId,
                              String productId,
                              ProductReplyListener replyTo);

    /**
     * 상품 요약 정보를 요청하는 콜백형 API.
     */
    void requestProductSummary(String inventoryId,
                               String productId,
                               ProductReplyListener replyTo);
}

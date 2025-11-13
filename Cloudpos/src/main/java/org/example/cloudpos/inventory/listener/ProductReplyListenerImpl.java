package org.example.cloudpos.inventory.listener;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.inventory.service.InventoryService;
import org.example.cloudpos.product.dto.ProductSummaryDto;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductReplyListenerImpl implements ProductReplyListener {

    private final InventoryService inventoryService;

    @Override
    public void onProductNotFound(String inventoryId, String productId) {
        inventoryService.handleProductNotFound(inventoryId, productId);
    }

    @Override
    public void onProductSellable(String inventoryId, String productId, boolean sellable) {
        inventoryService.handleSellableChecked(inventoryId, productId, sellable);
    }

    @Override
    public void onProductSummary(String inventoryId, String productId, org.example.cloudpos.product.dto.ProductSummaryDto summary) {
    }
}

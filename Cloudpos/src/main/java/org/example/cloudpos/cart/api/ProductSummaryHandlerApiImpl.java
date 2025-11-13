package org.example.cloudpos.cart.api;


import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.dto.ProductSummary;
import org.example.cloudpos.cart.exception.CartProductNotFoundException;
import org.example.cloudpos.inventory.listener.InventoryListener;
import org.example.cloudpos.product.dto.ProductSummaryDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductSummaryHandlerApiImpl implements ProductSummaryHandlerApi {

    private final InventoryListener inventoryListener;

    @Override
    @Transactional
    public ProductSummary getProductSummary(String productId) {
        ProductSummaryDto pv = inventoryListener.getProduct(productId);

        if (pv == null) throw new CartProductNotFoundException(productId);

        return new ProductSummary(
                pv.productId(),
                pv.name(),
                pv.price()
        );
    }
}

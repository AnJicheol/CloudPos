package org.example.cloudpos.cart.api;


import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.dto.ProductSummary;
import org.example.cloudpos.cart.exception.CartProductNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductSummaryHandlerApiImpl implements ProductSummaryHandlerApi {

    private final ProductSummaryRequestListener productListener;

    @Override
    @Transactional
    public ProductSummary getProductSummary(String productId) {
        ProductSummary pv = productListener.onProductSummaryRequest(productId);
        if (pv == null) throw new CartProductNotFoundException(productId);
        return pv;
    }


}

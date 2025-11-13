package org.example.cloudpos.cart.api;


import org.example.cloudpos.cart.dto.ProductSummary;

public interface ProductSummaryHandlerApi {
    ProductSummary getProductSummary(String productId);
}

package org.example.cloudpos.product.dto;

import org.example.cloudpos.product.ProductStatus;

public record ProductUpdateRequest(
        String name,
        Integer price,
        ProductStatus status
) {}

package org.example.cloudpos.cart.dto;


public record ProductSummary(
        String productId,
        String name,
        int price
) {}

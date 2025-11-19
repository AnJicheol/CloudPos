package org.example.cloudpos.order.dto;

import lombok.Builder;

@Builder
public record CartDto(String productId, int price, int quantity) {
}

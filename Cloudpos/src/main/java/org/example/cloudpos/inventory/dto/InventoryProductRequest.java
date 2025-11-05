package org.example.cloudpos.inventory.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 매장에 상품을 추가할 때 사용하는 요청 DTO.
 */
public record InventoryProductRequest(
        @NotNull Long productId
) {}

package org.example.cloudpos.inventory.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 매장 생성 요청 DTO.
 */
public record InventoryCreateRequest(
        @NotBlank String name
) {}

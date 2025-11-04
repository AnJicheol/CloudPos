package org.example.cloudpos.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 인벤토리(매장) 생성 요청 DTO입니다.
 *
 * @param name 매장명
 * @param productId 참조할 상품 ID
 */
public record InventoryCreateRequest(
        @NotBlank
        String name,

        @NotNull
        Long productId
) {}

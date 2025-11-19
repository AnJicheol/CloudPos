package org.example.cloudpos.discount.dto.owner.create;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record DiscountCreateIdRequest(
        @NotBlank String discountId,
        @NotBlank String inventoryId,
        @NotBlank String productId,
        String name,
        Integer amount,
        LocalDateTime discountStart,
        LocalDateTime discountEnd
) {

}

package org.example.cloudpos.discount.dto.owner.delete;

import java.time.LocalDateTime;

public record DiscountDeleteRequest(
        String discountId,
        String inventoryId,
        String productId,
        String name,
        Integer amount,
        LocalDateTime discountStart,
        LocalDateTime discountEnd
) {
}

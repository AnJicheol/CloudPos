package org.example.cloudpos.discount.dto.kiosk.select;

import java.time.LocalDateTime;

public record DiscountSelectRequest(
        String productId,
        String inventoryId,
        String discountId,
        String name,
        Integer amount,
        LocalDateTime discountStart,
        LocalDateTime discountEnd
) { }

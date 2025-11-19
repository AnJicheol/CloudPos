package org.example.cloudpos.discount.dto.kiosk;

import java.time.LocalDateTime;

public record DiscountKioskResponse(
        String inventoryId,
        String productId,
        String discountId,
        String name,
        Integer amount,
        LocalDateTime discountStart,
        LocalDateTime discountEnd
) {
    public static DiscountKioskResponse from(
            String inventoryId,
            String productId,
            String discountId,
            String name,
            Integer amount,
            LocalDateTime discountStart,
            LocalDateTime discountEnd
    ){
        return new DiscountKioskResponse(inventoryId, productId, discountId, name, amount, discountStart, discountEnd);
    }
}

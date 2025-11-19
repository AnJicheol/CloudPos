package org.example.cloudpos.discount.dto.kiosk;

import org.example.cloudpos.discount.domain.Discount;

import java.time.LocalDateTime;

public record DiscountKioskRequest(
        String productId,
        String inventoryId,
        String discountId,
        String name,
        Integer amount,
        LocalDateTime discountStart,
        LocalDateTime discountEnd
) {
    public static DiscountKioskRequest from(Discount d) {
        return new DiscountKioskRequest(
                d.getProductId(),
                d.getInventoryId(),
                d.getDiscountId(),
                d.getName(),
                d.getAmount(),
                d.getDiscountStart(),
                d.getDiscountEnd()
        );
    }
}

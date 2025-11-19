package org.example.cloudpos.discount.dto.owner;

import java.time.LocalDateTime;

public record DiscountOwnerResponse (
        String inventoryId,
        String discountId,
        String productId,
        String name,
        Integer amount,
        LocalDateTime discountStart,
        LocalDateTime discountEnd
){
    public static DiscountOwnerResponse from(
            String inventoryId,
            String discountId,
            String productId,
            String name,
            Integer amount,
            LocalDateTime discountStart,
            LocalDateTime discountEnd
    ){
        return new DiscountOwnerResponse(inventoryId, discountId, productId, name, amount, discountStart, discountEnd);
    }
}

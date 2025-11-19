package org.example.cloudpos.discount.dto.owner.create;

import java.time.LocalDateTime;

public record DiscountCreateIdResponse(
        String name,
        Integer amount,
        LocalDateTime discountStart,
        LocalDateTime discountEnd
)
{
    public static DiscountCreateIdResponse from(
            String name,
            Integer amount,
            LocalDateTime discountStart,
            LocalDateTime discountEnd
    ){
        return new DiscountCreateIdResponse(name, amount, discountStart, discountEnd);
    }
}

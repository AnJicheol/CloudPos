package org.example.cloudpos.discount.dto.owner.update;

import java.time.LocalDateTime;

public record DiscountUpdateResponse(
        String name,
        Integer amount,
        LocalDateTime discountStart,
        LocalDateTime discountEnd
)
{
    public static DiscountUpdateResponse from(
            String name,
            Integer amount,
            LocalDateTime discountStart,
            LocalDateTime discountEnd
    ){
        return new DiscountUpdateResponse(name, amount, discountStart, discountEnd);
    }
}

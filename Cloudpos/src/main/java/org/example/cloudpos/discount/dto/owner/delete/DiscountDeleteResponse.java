package org.example.cloudpos.discount.dto.owner.delete;

public record DiscountDeleteResponse(
        String result
)
{
    public static DiscountDeleteResponse from(
            String result
    ){
        return new DiscountDeleteResponse(result);
    }
}

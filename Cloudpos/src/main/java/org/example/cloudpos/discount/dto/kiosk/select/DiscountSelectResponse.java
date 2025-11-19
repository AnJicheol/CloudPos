package org.example.cloudpos.discount.dto.kiosk.select;


public record DiscountSelectResponse(
        String productId,
        String inventoryId,
        String discountId,
        String name,
        Integer amount
        )
{
    public static DiscountSelectResponse from(
            String productId,
            String inventoryId,
            String customerDiscountId,
            String name,
            Integer amount
    )
    {
        return new DiscountSelectResponse(productId, inventoryId, customerDiscountId, name, amount);
    }
}

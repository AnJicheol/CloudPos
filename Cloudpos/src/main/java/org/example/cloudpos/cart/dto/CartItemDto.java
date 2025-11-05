package org.example.cloudpos.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.cloudpos.product.dto.ProductSummaryDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private ProductSummaryDto product;
    private int quantity;

}
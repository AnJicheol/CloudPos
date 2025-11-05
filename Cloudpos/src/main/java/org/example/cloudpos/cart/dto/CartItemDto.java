package org.example.cloudpos.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private ProductCartDto product;
    private int quantity;
}

package org.example.cloudpos.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
//@NoArgsConstructor
//@AllArgsConstructor
public class ProductCartDto {
    private Long productId;
    private String productName;
    private int price;
}

package org.example.cloudpos.cart.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummary{
    String productId;
    String name;
    long price;
}


package org.example.cloudpos.cart.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 장바구니에서 사용하는 상품 요약 정보 DTO.
 *
 * <p>상품의 ID, 이름, 가격만 포함하여
 * 장바구니나 주문 검증 시 사용됩니다.</p>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummary{
    String productId;
    String name;
    long price;
}


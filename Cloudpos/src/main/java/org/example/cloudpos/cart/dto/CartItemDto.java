package org.example.cloudpos.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.cloudpos.product.dto.ProductSummaryDto;
/**

 * 장바구니 내 단일 상품 항목을 표현하는 DTO입니다.
 *
 * <p><b>구성 요소</b></p>
 * <ul>
 * <li>{@code product} — 상품 요약 정보 객체 ({@link org.example.cloudpos.product.dto.ProductSummaryDto})</li>
 * <li>{@code quantity} — 해당 상품의 장바구니 내 수량</li>
 * </ul>
 *
 * <p><b>용도</b><br>
 * Redis에 저장된 상품 식별자와 수량 정보를 조합하여,
 * 상품 요약 데이터와 함께 API 응답 혹은 내부 로직에서 사용됩니다.
 * </p>
 *
 * <p><b>비고</b><br>
 * 이 DTO는 읽기 전용 데이터 전달에 사용되며,
 * 엔티티나 Redis 구조를 직접 노출하지 않기 위해 분리되어 있습니다.
 * </p>

 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private ProductSummaryDto product;
    private int quantity;

}
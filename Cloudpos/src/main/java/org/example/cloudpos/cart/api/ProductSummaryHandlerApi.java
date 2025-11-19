package org.example.cloudpos.cart.api;


import org.example.cloudpos.cart.dto.ProductSummary;
/**
 * <h2>ProductSummaryHandlerApi</h2>
 *
 * 장바구니(Cart) 도메인에서 상품 정보를 조회하기 위한 통합 인터페이스입니다.
 *
 * <p>상품의 상세 정보를 직접 조회하지 않고,
 * 다른 모듈(예: 재고 또는 상품 모듈)로부터
 * 필요한 최소한의 요약 정보만을 가져오기 위해 사용됩니다.</p>
 *
 * <p>구현체에서는 상품 유효성 검증과 예외 처리 로직을 포함할 수 있습니다.</p>
 */
public interface ProductSummaryHandlerApi {
    ProductSummary getProductSummary(String productId);
}

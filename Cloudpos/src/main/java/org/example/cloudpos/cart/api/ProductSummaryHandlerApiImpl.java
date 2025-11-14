package org.example.cloudpos.cart.api;


import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.dto.ProductSummary;
import org.example.cloudpos.cart.exception.CartProductNotFoundException;
import org.example.cloudpos.inventory.listener.InventoryListener;
import org.example.cloudpos.product.dto.ProductSummaryDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * <h2>ProductSummaryHandlerApiImpl</h2>
 *
 * 장바구니(Cart) 도메인에서 상품 요약 정보를 제공하는 API 구현체입니다.
 *
 * <p>재고 모듈의 {@link InventoryListener}를 통해 상품 정보를 조회하며,
 * 장바구니에 상품이 추가되거나 검증될 때 필요한
 * 최소한의 상품 속성(상품 ID, 이름, 가격)을 반환합니다.</p>
 *
 * <p>요청한 상품이 존재하지 않을 경우
 * {@link CartProductNotFoundException}을 발생시킵니다.</p>
 */
@Component
@RequiredArgsConstructor
public class ProductSummaryHandlerApiImpl implements ProductSummaryHandlerApi {

    private final InventoryListener inventoryListener;

    @Override
    @Transactional
    public ProductSummary getProductSummary(String productId) {
        ProductSummaryDto pv = inventoryListener.getProduct(productId);

        if (pv == null) throw new CartProductNotFoundException(productId);

        return new ProductSummary(
                pv.productId(),
                pv.name(),
                pv.price()
        );
    }
}

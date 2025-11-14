package org.example.cloudpos.inventory.listener;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.inventory.listener.InventoryListener;
import org.example.cloudpos.product.dto.ProductSummaryDto;
import org.example.cloudpos.product.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * <h2>InventoryListenerImpl</h2>
 *
 * 재고(Inventory) 모듈에서 상품 정보를 조회하여
 * 다른 도메인(Cart 등)에 전달하는 리스너 구현체입니다.
 *
 * <p>상품 저장소({@link ProductRepository})를 조회해
 * 요청된 상품의 요약 정보({@link ProductSummaryDto})를 반환하며,
 * 상품이 존재하지 않을 경우 {@code null}을 반환합니다.
 * (호출 측에서 예외 처리를 담당합니다.)</p>
 */
@Component
@RequiredArgsConstructor
public class InventoryListenerImpl implements InventoryListener {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public ProductSummaryDto getProduct(String productId) {

        return productRepository.findByProductId(productId)
                .map(p -> new ProductSummaryDto(
                        p.getProductId(),
                        p.getName(),
                        p.getPrice()
                ))
                .orElse(null);  // Cart에서 null이면 예외 던짐
    }
}

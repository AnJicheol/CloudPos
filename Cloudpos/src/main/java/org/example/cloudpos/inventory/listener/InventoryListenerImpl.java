package org.example.cloudpos.inventory.listener;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.inventory.listener.InventoryListener;
import org.example.cloudpos.product.dto.ProductSummaryDto;
import org.example.cloudpos.product.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

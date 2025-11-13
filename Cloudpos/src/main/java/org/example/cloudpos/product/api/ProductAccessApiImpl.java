package org.example.cloudpos.product.api;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.inventory.listener.ProductReplyListener;
import org.example.cloudpos.product.domain.ProductStatus;
import org.example.cloudpos.product.dto.ProductSummaryDto;
import org.example.cloudpos.product.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductAccessApiImpl implements ProductAccessApi {

    private final ProductRepository repo;

    @Override
    @Transactional(readOnly = true)
    public void requestSellableCheck(String inventoryId,
                                     String productId,
                                     ProductReplyListener replyTo) {

        var opt = repo.findByProductId(productId);

        if (opt.isEmpty()) {
            replyTo.onProductNotFound(inventoryId, productId);
            return;
        }

        var p = opt.get();
        boolean ok = p.getStatus() != ProductStatus.ARCHIVED;
        replyTo.onProductSellable(inventoryId, productId, ok);
    }

    @Override
    @Transactional(readOnly = true)
    public void requestProductSummary(String inventoryId,
                                      String productId,
                                      ProductReplyListener replyTo) {

        var opt = repo.findByProductId(productId);

        if (opt.isEmpty()) {
            replyTo.onProductNotFound(inventoryId, productId);
            return;
        }

        var p = opt.get();
        replyTo.onProductSummary(
                inventoryId,
                productId,
                new ProductSummaryDto(p.getProductId(), p.getName(), p.getPrice())
        );
    }
}
package org.example.cloudpos.inventory.service;

import com.github.f4b6a3.ulid.UlidCreator;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.inventory.domain.Inventory;
import org.example.cloudpos.inventory.dto.InventoryCreateRequest;
import org.example.cloudpos.inventory.dto.InventoryResponse;
import org.example.cloudpos.inventory.repository.InventoryRepository;
import org.example.cloudpos.product.domain.Product;
import org.example.cloudpos.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

/**
 * 인벤토리(매장) 비즈니스 로직 서비스입니다.
 *
 * <p>상품(Product)을 참조하여 인벤토리를 등록하거나 삭제할 수 있습니다.</p>
 * <p>{@code inventoryId}는 {@link UlidCreator}를 이용해 ULID 형식으로 생성됩니다.</p>
 *
 * @author
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepo;
    private final ProductRepository productRepo;

    /**
     * 인벤토리(매장)를 등록합니다.
     *
     * @param req 인벤토리 생성 요청 DTO
     * @return 생성된 인벤토리 응답 DTO
     */
    public InventoryResponse create(InventoryCreateRequest req) {
        Product product = productRepo.findById(req.productId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + req.productId()));

        // ✅ ULID 생성 (Product와 동일한 방식)
        String ulid = UlidCreator.getUlid().toString();

        Inventory saved = inventoryRepo.save(new Inventory(ulid, req.name(), product));

        return new InventoryResponse(
                saved.getId(),
                saved.getInventoryId(),
                saved.getName(),
                product.getId(),
                product.getName()
        );
    }

    /**
     * 인벤토리(매장)를 삭제합니다.
     *
     * @param inventoryId 삭제할 인벤토리의 외부 식별자(ULID)
     */
    public void delete(String inventoryId) {
        inventoryRepo.deleteByInventoryId(inventoryId);
    }
}

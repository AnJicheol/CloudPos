package org.example.cloudpos.inventory.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.inventory.domain.Inventory;
import org.example.cloudpos.inventory.repository.InventoryRepository;
import org.example.cloudpos.product.domain.Product;
import org.example.cloudpos.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 인벤토리(매장) 비즈니스 로직을 처리하는 서비스입니다.
 *
 * <p>인벤토리는 점주의 매장 단위를 나타내며,
 * 본사 상품(Product)을 참조합니다.</p>
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
     * 새로운 인벤토리(매장)를 등록합니다.
     *
     * @param name 인벤토리(매장) 이름
     * @param productId 연결할 상품 ID
     * @return 생성된 인벤토리의 외부 식별자(ULID)
     */
    public String create(String name, Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        String ulid = UUID.randomUUID().toString(); // 실제로는 ULID 생성 로직으로 교체 가능
        Inventory inventory = new Inventory(ulid, name, product);
        inventoryRepo.save(inventory);
        return ulid;
    }

    /**
     * 인벤토리를 삭제합니다.
     *
     * @param inventoryId 삭제할 인벤토리의 외부 식별자(ULID)
     */
    public void delete(String inventoryId) {
        inventoryRepo.deleteByInventoryId(inventoryId);
    }
}

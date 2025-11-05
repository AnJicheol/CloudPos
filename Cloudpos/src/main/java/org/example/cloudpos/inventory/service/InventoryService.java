package org.example.cloudpos.inventory.service;

import com.github.f4b6a3.ulid.UlidCreator;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.inventory.domain.Inventory;
import org.example.cloudpos.inventory.dto.InventoryCreateRequest;
import org.example.cloudpos.inventory.dto.InventoryProductResponse;
import org.example.cloudpos.inventory.exception.DuplicateStoreProductException;
import org.example.cloudpos.inventory.exception.InventoryNotFoundException;
import org.example.cloudpos.inventory.repository.InventoryRepository;
import org.example.cloudpos.product.domain.Product;
import org.example.cloudpos.product.exception.ProductNotFoundException;
import org.example.cloudpos.product.repository.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 인벤토리(매장) 관련 비즈니스 로직을 처리합니다.
 *
 * <p>FK는 Inventory.product_id로 구성되며,
 * 한 매장에 여러 상품이 속할 수 있고,
 * 하나의 상품은 하나의 매장에만 등록됩니다.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepo;
    private final ProductRepository productRepo;

    /** 매장 생성 */
    public String create(InventoryCreateRequest req) {
        String ulid = UlidCreator.getUlid().toString();
        Inventory inventory = new Inventory(ulid, req.name(), null);
        inventoryRepo.save(inventory);
        return ulid;
    }

    /**
     * 매장에 상품을 추가합니다.
     *
     * <p>상품은 본사 기준(Product) 데이터를 참조하며,
     * Inventory가 상품을 소유하는 형태(1:N)로 관리됩니다.</p>
     */
    @Transactional
    public void addProduct(String inventoryId, Long productId) {
        // 상품 존재 확인
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // 매장 존재 확인 (inventoryId 기준)
        Inventory inventory = inventoryRepo.findFirstByInventoryId(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryId));

        // 매장 이름 스냅샷 복제해서 INSERT
        Inventory newRow = new Inventory(inventoryId, inventory.getName(), product);
        try {
            inventoryRepo.save(newRow);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateStoreProductException(inventoryId, productId, e);
        }
    }




    /** 매장의 상품 목록 조회 */
    @Transactional(readOnly = true)
    public List<InventoryProductResponse> listProducts(String inventoryId) {
        // 1) 매장 존재 검증
        if (!inventoryRepo.existsByInventoryId(inventoryId)) {
            throw new IllegalArgumentException("해당 매장이 존재하지 않습니다.");
        }
        // 2) N+1 방지용 fetch join 사용
        return inventoryRepo.findAllWithProductByInventoryId(inventoryId).stream()
                .map(inv -> InventoryProductResponse.from(inv.getProduct()))
                .toList();
    }

    /** 매장에서 상품 제거 */
    @Transactional
    public void removeProduct(String inventoryId, Long productId) {
        long deleted = inventoryRepo.deleteByInventoryIdAndProduct_Id(inventoryId, productId);
        if (deleted == 0) {
            // 매장 자체가 없거나, 해당 매장에 그 상품이 없을 때
            throw new IllegalArgumentException("해당 매장에서 해당 상품을 찾을 수 없습니다.");
        }
    }

}

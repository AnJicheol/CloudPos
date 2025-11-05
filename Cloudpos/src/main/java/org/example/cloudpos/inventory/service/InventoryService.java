package org.example.cloudpos.inventory.service;

import com.github.f4b6a3.ulid.UlidCreator;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.inventory.domain.Inventory;
import org.example.cloudpos.inventory.dto.InventoryCreateRequest;
import org.example.cloudpos.inventory.dto.InventoryProductResponse;
import org.example.cloudpos.inventory.repository.InventoryRepository;
import org.example.cloudpos.product.domain.Product;
import org.example.cloudpos.product.repository.ProductRepository;
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
        // 1) 상품 존재 확인
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // 2) 전역 중복 보호: 이 상품이 이미 다른 매장에 등록되어 있으면 막기
        if (inventoryRepo.existsByProduct_Id(productId)) {
            throw new IllegalStateException("해당 상품은 이미 다른 매장에 등록되어 있습니다.");
        }

        // 3) 매장(ULID) 존재 및 이름 확보
        String name = inventoryRepo.findFirstByInventoryId(inventoryId)
                .map(Inventory::getName)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다. 먼저 매장을 생성하세요."));

        // 4) 같은 매장에 같은 상품 중복 등록 방지(안전망)
        if (inventoryRepo.existsByInventoryIdAndProduct_Id(inventoryId, productId)) {
            throw new IllegalStateException("이미 해당 매장에 등록된 상품입니다.");
        }

        // 5) 매장-상품 한 줄 INSERT
        Inventory row = new Inventory(inventoryId, name, product);
        inventoryRepo.save(row);
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

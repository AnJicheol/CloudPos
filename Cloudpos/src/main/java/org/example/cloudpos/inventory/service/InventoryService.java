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
 * 인벤토리(매장) 관련 비즈니스 로직을 처리하는 서비스 클래스.
 *
 * <p>매장(Inventory)과 본사 상품(Product) 간의 매핑 관계를 관리합니다.</p>
 *
 * <h2>도메인 관계</h2>
 * <ul>
 *   <li>하나의 매장(Inventory)은 여러 상품을 가질 수 있습니다.</li>
 *   <li>하나의 상품(Product)은 오직 하나의 매장에만 등록될 수 있습니다.</li>
 * </ul>
 *
 * <h2>트랜잭션 정책</h2>
 * <ul>
 *   <li>클래스 전체에 {@link Transactional}이 적용되어 기본적으로 트랜잭션 내에서 수행됩니다.</li>
 *   <li>조회 메서드는 {@code readOnly = true}로 설정하여 읽기 전용 트랜잭션을 사용합니다.</li>
 * </ul>
 *
 * @see org.example.cloudpos.inventory.repository.InventoryRepository
 * @see org.example.cloudpos.product.repository.ProductRepository
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepo;
    private final ProductRepository productRepo;

    /**
     * 신규 매장을 생성합니다.
     *
     * <p>매장은 ULID를 외부 식별자로 사용하며, 생성 시 상품은 포함되지 않습니다.</p>
     *
     * @param req 매장 생성 요청 DTO
     * @return 생성된 매장의 ULID
     */
    public String create(InventoryCreateRequest req) {
        String ulid = UlidCreator.getUlid().toString();
        Inventory inventory = new Inventory(ulid, req.name(), null);
        inventoryRepo.save(inventory);
        return ulid;
    }

    /**
     * 매장에 상품을 추가합니다.
     *
     * <p>본사 상품(Product)을 참조하여 매장에 등록합니다.
     * 하나의 상품은 하나의 매장에만 등록될 수 있으며,
     * 중복 등록 시 {@link DuplicateStoreProductException}이 발생합니다.</p>
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @param productId 등록할 상품의 ID
     * @throws ProductNotFoundException 지정한 상품이 존재하지 않을 경우
     * @throws InventoryNotFoundException 지정한 매장이 존재하지 않을 경우
     * @throws DuplicateStoreProductException 동일 상품이 이미 등록된 경우
     */
    @Transactional
    public void addProduct(String inventoryId, String productId) {
        // 상품 존재 확인
        Product product = productRepo.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // 매장 존재 확인
        Inventory inventory = inventoryRepo.findFirstByInventoryId(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryId));

        // 매장 이름 스냅샷 복제 후 저장
        Inventory newRow = new Inventory(inventoryId, inventory.getName(), product);
        try {
            inventoryRepo.save(newRow);
        } catch (DataIntegrityViolationException e) {
            // UNIQUE 제약 위반 시 도메인 예외로 변환
            throw new DuplicateStoreProductException(inventoryId, productId, e);
        }
    }

    /**
     * 특정 매장에 등록된 상품 목록을 조회합니다.
     *
     * <p>매장이 존재하지 않으면 예외를 던지며,
     * 조회 시 N+1 문제를 방지하기 위해 {@code fetch join}을 사용합니다.</p>
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @return 매장 내 상품 정보를 담은 DTO 목록
     * @throws IllegalArgumentException 지정한 매장이 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public List<InventoryProductResponse> listProducts(String inventoryId) {
        // 매장 존재 검증
        if (!inventoryRepo.existsByInventoryId(inventoryId)) {
            throw new IllegalArgumentException("해당 매장이 존재하지 않습니다.");
        }
        // N+1 방지용 fetch join
        return inventoryRepo.findAllWithProductByInventoryId(inventoryId).stream()
                .map(inv -> InventoryProductResponse.from(inv.getProduct()))
                .toList();
    }

    /**
     * 매장에서 특정 상품을 제거합니다.
     *
     * <p>매장-상품 매핑 테이블에서 해당 상품을 삭제하며,
     * 매장이나 상품 엔티티 자체에는 영향을 주지 않습니다.</p>
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @param productId 상품 기본키 ID
     * @throws IllegalArgumentException 매장이 존재하지 않거나 해당 상품이 매장에 없을 경우
     */
    @Transactional
    public void removeProduct(String inventoryId, String productId) {
        long deleted = inventoryRepo.deleteByInventoryIdAndProduct_ProductId(inventoryId, productId);
        if (deleted == 0) {
            throw new IllegalArgumentException("해당 매장에서 해당 상품을 찾을 수 없습니다.");
        }
    }
}

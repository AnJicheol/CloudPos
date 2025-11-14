package org.example.cloudpos.inventory.service;

import com.github.f4b6a3.ulid.UlidCreator;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.inventory.domain.Inventory;
import org.example.cloudpos.inventory.domain.InventoryProductStatus;
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
public class InventoryServiceImpl implements InventoryService {

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
    @Override
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
     * 하나의 상품은 하나의 매장에만 등록될 수 있습니다.</p>
     *
     * <p>이미 동일 상품 매핑이 존재하는 경우,
     * 상태가 {@code REMOVED}라면 다시 {@code ACTIVE}로 복원하며,
     * 이미 {@code ACTIVE}라면 {@link DuplicateStoreProductException}을 발생시킵니다.</p>
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @param productId   등록할 상품의 ID
     * @throws ProductNotFoundException    지정한 상품이 존재하지 않을 경우
     * @throws InventoryNotFoundException  지정한 매장이 존재하지 않을 경우
     * @throws DuplicateStoreProductException 동일 상품이 이미 ACTIVE 상태로 등록된 경우
     */
    @Override
    @Transactional
    public void addProduct(String inventoryId, String productId) {
        // 상품 존재 확인
        Product product = productRepo.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // 매장 존재 확인 (헤더 레코드 기준)
        Inventory header = inventoryRepo.findFirstByInventoryId(inventoryId);
        if (header == null) {
            throw new InventoryNotFoundException(inventoryId);
        }

        // 기존 매핑 있는지 확인
        Inventory existing =
                inventoryRepo.findByInventoryIdAndProduct_ProductId(inventoryId, productId);

        if (existing != null) {
            // 예전에 제거된 상태면 다시 활성화
            if (existing.getStatus() == InventoryProductStatus.REMOVED) {
                existing.markActive();
                return;
            }
            // 이미 ACTIVE면 중복 등록 예외
            throw new DuplicateStoreProductException(inventoryId, productId);
        }

        // 완전히 새로운 매핑이면 새 행 생성
        Inventory newRow = new Inventory(inventoryId, header.getName(), product);
        inventoryRepo.save(newRow);
    }


    /**
     * 특정 매장에 등록된 상품 목록을 조회합니다.
     *
     * <p>매장이 존재하지 않으면 예외를 던지며,
     * 조회 시 N+1 문제를 방지하기 위해 {@code fetch join}을 사용합니다.</p>
     *
     * <p>상태가 {@code ACTIVE}인 매장-상품 매핑만 조회되므로,
     * 소프트 삭제된({@code REMOVED}) 상품은 결과에 포함되지 않습니다.</p>
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @return 매장 내 ACTIVE 상태 상품 정보를 담은 DTO 목록
     * @throws InventoryNotFoundException 지정한 매장이 존재하지 않을 경우
     */
    @Override
    @Transactional(readOnly = true)
    public List<InventoryProductResponse> listProducts(String inventoryId) {
        // 매장 존재 검증
        if (!inventoryRepo.existsByInventoryId(inventoryId)) {
            throw new InventoryNotFoundException(inventoryId);
        }

        // ACTIVE 인 것만 조회
        return inventoryRepo.findActiveProductsByInventoryId(inventoryId).stream()
                .map(inv -> InventoryProductResponse.from(inv.getProduct()))
                .toList();
    }

    /**
     * 매장에서 특정 상품을 제거합니다.
     *
     * <p>매장-상품 매핑 행을 삭제하거나 연관관계를 끊지 않고,
     * 상태를 {@code REMOVED}로 변경하는 방식의 소프트 삭제를 수행합니다.</p>
     *
     * <p>소프트 삭제된 상품은 이후 {@link #listProducts(String)} 조회 시
     * 결과에 포함되지 않습니다.</p>
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @param productId   상품 식별자
     * @throws IllegalArgumentException 지정한 매장에서 해당 상품 매핑을 찾을 수 없을 경우
     */
    @Override
    @Transactional
    public void removeProduct(String inventoryId, String productId) {
        Inventory inventory =
                inventoryRepo.findByInventoryIdAndProduct_ProductId(inventoryId, productId);

        if (inventory == null) {
            throw new IllegalArgumentException("해당 매장에서 해당 상품을 찾을 수 없습니다.");
        }

        inventory.markRemoved(); // product는 그대로, status만 REMOVED
    }
}

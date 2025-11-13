package org.example.cloudpos.inventory.service;

import com.github.f4b6a3.ulid.UlidCreator;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.inventory.domain.Inventory;
import org.example.cloudpos.inventory.dto.InventoryCreateRequest;
import org.example.cloudpos.inventory.dto.InventoryProductResponse;
import org.example.cloudpos.inventory.exception.DuplicateStoreProductException;
import org.example.cloudpos.inventory.exception.InventoryNotFoundException;
import org.example.cloudpos.inventory.repository.InventoryRepository;
import org.example.cloudpos.product.api.ProductAccessApi;
import org.example.cloudpos.inventory.listener.ProductReplyListener;
import org.example.cloudpos.product.domain.Product;
import org.example.cloudpos.product.dto.ProductSummaryDto;
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
    private final ProductAccessApi productAccessApi;
    /** Product 모듈에서 제공하는 콜백 인터페이스 구현체 (Inventory 쪽에 있음) */
    private final ProductReplyListener inventoryReplyListener;

    /**
     * 신규 매장을 생성합니다.
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
     * <p>콜백형 API를 사용하여, 먼저 상품 판매 가능 여부를 Product 모듈에 요청하고,
     * 응답은 {@link ProductReplyListener} 구현체에서 받아
     * {@link #handleSellableChecked(String, String, boolean)} 로 위임합니다.</p>
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @param productId 등록할 상품의 ID
     */
    public void addProduct(String inventoryId, String productId) {
        productAccessApi.requestSellableCheck(
                inventoryId,
                productId,
                inventoryReplyListener   //  필드 그대로 넘김 (inventoryReplyListener())
        );
    }

    /**
     * 콜백: 상품 판매 가능 여부 응답 처리.
     *
     * @param inventoryId 매장 외부 식별자
     * @param productId 상품 식별자
     * @param sellable 판매 가능 여부
     */
    public void handleSellableChecked(String inventoryId, String productId, boolean sellable) {
        if (!sellable) {
            throw new IllegalStateException("판매 불가 상태의 상품입니다.");
        }

        // 판매 가능 → 실제 진열 등록
        Product product = productRepo.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Inventory inventory = inventoryRepo.findFirstByInventoryId(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryId));

        Inventory row = new Inventory(inventoryId, inventory.getName(), product);

        try {
            inventoryRepo.save(row);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateStoreProductException(inventoryId, productId, e);
        }
    }

    /**
     * 콜백: 상품 없음.
     *
     * @param inventoryId 매장 외부 식별자
     * @param productId 상품 식별자
     */
    public void handleProductNotFound(String inventoryId, String productId) {
        throw new ProductNotFoundException(productId);
    }

    /**
     * 특정 매장에 등록된 상품 목록을 조회합니다.
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @return 매장 내 상품 정보를 담은 DTO 목록
     * @throws IllegalArgumentException 지정한 매장이 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public List<InventoryProductResponse> listProducts(String inventoryId) {
        if (!inventoryRepo.existsByInventoryId(inventoryId)) {
            throw new IllegalArgumentException("해당 매장이 존재하지 않습니다.");
        }
        return inventoryRepo.findAllWithProductByInventoryId(inventoryId).stream()
                .map(inv -> InventoryProductResponse.from(inv.getProduct()))
                .toList();
    }

    /**
     * 매장에서 특정 상품을 제거합니다.
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @param productId 상품 식별자
     */
    public void removeProduct(String inventoryId, String productId) {
        long deleted = inventoryRepo.deleteByInventoryIdAndProduct_ProductId(inventoryId, productId);
        if (deleted == 0) {
            throw new IllegalArgumentException("해당 매장에서 해당 상품을 찾을 수 없습니다.");
        }
    }
}

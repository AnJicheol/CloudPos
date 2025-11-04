package org.example.cloudpos.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.inventory.service.InventoryService;
import org.example.cloudpos.product.domain.ProductStatus;
import org.example.cloudpos.product.dto.ProductCreateRequest;
import org.example.cloudpos.product.dto.ProductResponse;
import org.example.cloudpos.product.dto.ProductUpdateRequest;
import org.example.cloudpos.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * 인벤토리(매장) 및 상품 관련 REST API 통합 컨트롤러입니다.
 *
 * <p>점주(User)의 매장(Inventory) 등록/삭제와
 * 상품(Product)의 생성/조회/수정/삭제 기능을 함께 제공합니다.</p>
 *
 * <pre>
 * Base URL:
 *  - 인벤토리 관련: /api/inventories
 *  - 상품 관련:     /api/inventories/products
 * </pre>
 *
 * @author Esther
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ProductService productService;

    /* -------------------------------------
       🎯 인벤토리 (매장) 관련 API
    ------------------------------------- */

    /**
     * 인벤토리(매장) 등록
     *
     * @param name 매장명
     * @param productId 연결할 상품 ID
     * @return 생성된 인벤토리 Location 헤더
     */
    @PostMapping
    public ResponseEntity<Void> createInventory(
            @RequestParam String name,
            @RequestParam Long productId
    ) {
        String inventoryId = inventoryService.create(name, productId);
        return ResponseEntity.created(URI.create("/api/inventories/" + inventoryId)).build();
    }

    /**
     * 인벤토리(매장) 삭제
     *
     * @param inventoryId 인벤토리 ULID
     * @return 본문 없는 {@code 204 No Content} 응답
     */
    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable String inventoryId) {
        inventoryService.delete(inventoryId);
        return ResponseEntity.noContent().build();
    }

    /* -------------------------------------
       🎯 상품 (Product) 관련 API
    ------------------------------------- */

    /**
     * 신규 상품 등록
     *
     * @param req 상품 생성 요청 DTO
     * @return 생성된 상품 정보와 Location 헤더
     */
    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest req) {
        Long id = productService.create(req);
        ProductResponse body = productService.get(id);
        return ResponseEntity.created(URI.create("/api/inventories/products/" + id)).body(body);
    }

    /**
     * 상품 단건 조회
     *
     * @param id 상품 기본키 ID
     * @return 상품 상세 정보
     */
    @GetMapping("/products/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.get(id);
    }

    /**
     * 상품 목록 조회 (페이지네이션)
     *
     * @param pageable 페이지 요청 정보
     * @return 상품 목록
     */
    @GetMapping("/products")
    public Page<ProductResponse> listProducts(Pageable pageable) {
        return productService.list(pageable);
    }

    /**
     * 상품 정보 수정
     *
     * @param id 수정할 상품 ID
     * @param req 수정 요청 DTO
     * @return 본문 없는 {@code 204 No Content}
     */
    @PatchMapping("/products/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequest req) {
        productService.update(id, req);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 삭제(아카이브 처리)
     *
     * <p>상태를 {@link ProductStatus#ARCHIVED} 로 변경합니다.</p>
     *
     * @param id 상품 기본키 ID
     * @return 본문 없는 {@code 204 No Content}
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> archiveProduct(@PathVariable Long id) {
        productService.archive(id);
        return ResponseEntity.noContent().build();
    }
}

package org.example.cloudpos.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.inventory.dto.InventoryCreateRequest;
import org.example.cloudpos.inventory.dto.InventoryProductRequest;
import org.example.cloudpos.inventory.dto.InventoryProductResponse;
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
import java.util.List;

/**
 * 인벤토리(매장) 관련 REST API 컨트롤러.
 *
 * <p>매장 생성, 매장별 상품 등록 및 관리 기능을 제공합니다.</p>
 *
 * <ul>
 *     <li>매장 생성</li>
 *     <li>매장에 상품 추가</li>
 *     <li>매장 내 상품 조회</li>
 *     <li>매장 내 상품 삭제</li>
 * </ul>
 *
 * <p>상품(Product)의 등록, 수정, 삭제는 본사 전용 ProductController에서 담당합니다.</p>
 *
 * @author Esther
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final ProductService productService;

    // ---------------------------
    // Inventory (매장) 영역
    // ---------------------------

    /**
     * 신규 매장을 생성합니다.
     *
     * Base: POST /api/inventories
     */
    @PostMapping("/api/inventories")
    public ResponseEntity<String> createInventory(@Valid @RequestBody InventoryCreateRequest req) {
        String inventoryId = inventoryService.create(req);
        return ResponseEntity.created(URI.create("/api/inventories/" + inventoryId)).body(inventoryId);
    }

    /**
     * 매장에 상품을 추가합니다.
     *
     * Base: POST /api/inventories/{inventoryId}/products
     */
    @PostMapping("/api/inventories/{inventoryId}/products")
    public ResponseEntity<Void> addProductToInventory(
            @PathVariable String inventoryId,
            @Valid @RequestBody InventoryProductRequest req
    ) {
        inventoryService.addProduct(inventoryId, req.productId());
        return ResponseEntity.ok().build(); // 원본 유지 (필요시 204/201로 팀 합의)
    }

    /**
     * 특정 매장에 등록된 상품 목록을 조회합니다.
     *
     * Base: GET /api/inventories/{inventoryId}/products
     */
    @GetMapping("/api/inventories/{inventoryId}/products")
    public List<InventoryProductResponse> listInventoryProducts(@PathVariable String inventoryId) {
        return inventoryService.listProducts(inventoryId);
    }

    /**
     * 매장에서 특정 상품을 제거합니다.
     *
     * Base: DELETE /api/inventories/{inventoryId}/products/{productId}
     */
    @DeleteMapping("/api/inventories/{inventoryId}/products/{productId}")
    public ResponseEntity<Void> removeInventoryProduct(
            @PathVariable String inventoryId,
            @PathVariable Long productId
    ) {
        inventoryService.removeProduct(inventoryId, productId);
        return ResponseEntity.noContent().build();
    }

    // ---------------------------
    // Product (상품) 영역
    // ---------------------------

    /**
     * 신규 상품을 등록합니다.
     *
     * Base: POST /api/products
     *
     * (원본 그대로: service.create → id 받고, 다시 service.get 호출)
     * ※ 2회 조회 줄이는 리팩터링은 나중에 적용하기로 했던 내용이라 유지.
     */
    @PostMapping("/api/products")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest req) {
        Long id = productService.create(req);
        ProductResponse body = productService.get(id);
        return ResponseEntity.created(URI.create("/api/products/" + id)).body(body);
    }

    /**
     * 상품을 ID로 조회합니다.
     *
     * Base: GET /api/products/{id}
     */
    @GetMapping("/api/products/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.get(id);
    }

    /**
     * 상품 목록을 페이지 단위로 조회합니다.
     *
     * Base: GET /api/products
     */
    @GetMapping("/api/products")
    public Page<ProductResponse> listProducts(Pageable pageable) {
        return productService.list(pageable);
    }

    /**
     * 기존 상품 정보를 수정합니다. (부분 업데이트)
     *
     * Base: PATCH /api/products/{id}
     */
    @PatchMapping("/api/products/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequest req) {
        productService.update(id, req);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품을 삭제(아카이브)합니다.
     *
     * Base: DELETE /api/products/{id}
     * 상태를 {@link ProductStatus#ARCHIVED} 로 변경하는 소프트 삭제.
     */
    @DeleteMapping("/api/products/{id}")
    public ResponseEntity<Void> archiveProduct(@PathVariable Long id) {
        productService.archive(id);
        return ResponseEntity.noContent().build();
    }
}
package org.example.cloudpos.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.inventory.dto.InventoryCreateRequest;
import org.example.cloudpos.inventory.dto.InventoryResponse;
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
 * 인벤토리(매장) 및 상품(Product) 관련 통합 컨트롤러입니다.
 *
 * <p>점주(User)가 자신의 매장을 등록/삭제하고,
 * 본사 상품(Product)을 생성·조회·수정·삭제할 수 있도록 합니다.</p>
 *
 * <pre>
 * Base URL:
 *  - 인벤토리 관련: /api/inventories
 *  - 상품 관련:     /api/inventories/products
 * </pre>
 *
 * @author
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ProductService productService;

    /* ------------------------------------------------------
       인벤토리(매장) 관련 API

    /**
     * 인벤토리(매장)를 등록합니다.
     *
     * <p>요청 본문에는 매장명(name)과 연결할 상품 ID(productId)가 포함됩니다.</p>
     *
     * @param req 인벤토리 생성 요청 DTO
     * @return 생성된 인벤토리 응답 DTO
     */
    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@Valid @RequestBody InventoryCreateRequest req) {
        InventoryResponse body = inventoryService.create(req);
        return ResponseEntity.created(URI.create("/api/inventories/" + body.inventoryId())).body(body);
    }

    /**
     * 인벤토리(매장)를 삭제합니다.
     *
     * <p>ULID를 이용해 인벤토리를 식별하며,
     * 존재하지 않는 ID를 삭제하려 할 경우 예외가 발생할 수 있습니다.</p>
     *
     * @param inventoryId 삭제할 인벤토리의 외부 식별자(ULID)
     * @return 본문 없는 {@code 204 No Content} 응답
     */
    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable String inventoryId) {
        inventoryService.delete(inventoryId);
        return ResponseEntity.noContent().build();
    }

    /* ------------------------------------------------------
       상품(Product) 관련 API (기존 ProductController 이관)
       ------------------------------------------------------ */

    /**
     * 신규 상품을 등록합니다.
     *
     * <p>{@code productId}는 서버에서 ULID로 자동 생성되며,
     * 요청 본문에는 상품명, 가격, 상태, 이미지 URL 등을 포함할 수 있습니다.</p>
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
     * 상품을 ID로 조회합니다.
     *
     * @param id 상품 기본키 ID
     * @return 상품 상세 정보
     */
    @GetMapping("/products/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.get(id);
    }

    /**
     * 상품 목록을 페이지 단위로 조회합니다.
     *
     * <p>아카이브(ARCHIVED) 상태가 아닌 상품만 반환합니다.</p>
     *
     * @param pageable 페이지 요청 정보
     * @return 상품 목록 페이지
     */
    @GetMapping("/products")
    public Page<ProductResponse> listProducts(Pageable pageable) {
        return productService.list(pageable);
    }

    /**
     * 기존 상품 정보를 수정합니다.
     *
     * <p>요청 본문에는 수정할 필드만 포함할 수 있으며,
     * null로 전달된 필드는 변경되지 않습니다.</p>
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
     * 상품을 삭제(아카이브 처리)합니다.
     *
     * <p>실제 DB에서 삭제하지 않고 상태를 {@link ProductStatus#ARCHIVED}로 변경합니다.</p>
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

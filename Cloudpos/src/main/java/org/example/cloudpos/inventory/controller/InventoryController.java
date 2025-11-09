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
 * 통합 상품 및 인벤토리(매장) API 컨트롤러.
 *
 * <p>상품(Product) 및 매장(Inventory)에 대한 생성, 수정, 삭제, 조회 등의
 * REST API 엔드포인트를 제공합니다.</p>
 *
 * <p>원래 분리되어 있던 {@code ProductController} 와 {@code InventoryController} 의 기능을
 * MVP 환경에서 간단히 통합한 버전입니다. 이후 모듈별 분리 시 이 클래스를 나눠 사용합니다.</p>
 *
 * <h2>제공 기능</h2>
 * <ul>
 *   <li>상품 등록, 조회, 수정, 삭제 (ProductService)</li>
 *   <li>매장 생성, 매장별 상품 등록 및 관리 (InventoryService)</li>
 * </ul>
 *
 * <h2>Base URLs</h2>
 * <ul>
 *   <li>{@code /api/products}</li>
 *   <li>{@code /api/inventories}</li>
 * </ul>
 *
 * @author Esther
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final ProductService productService;

    // ==========================================================
    // Inventory (매장) 관련 엔드포인트
    // ==========================================================

    /**
     * 신규 매장을 생성합니다.
     *
     * <p>매장은 ULID를 외부 식별자로 사용하며,
     * 생성 시 상품은 포함되지 않습니다.</p>
     *
     * @param req 매장 생성 요청 DTO (이름 등 기본정보 포함)
     * @return 생성된 매장의 ULID를 본문으로 반환하며,
     *         Location 헤더에 해당 매장의 리소스 경로를 포함합니다.
     * @status 201 Created
     */
    @PostMapping("/api/inventories")
    public ResponseEntity<String> createInventory(@Valid @RequestBody InventoryCreateRequest req) {
        String inventoryId = inventoryService.create(req);
        return ResponseEntity.created(URI.create("/api/inventories/" + inventoryId)).body(inventoryId);
    }

    /**
     * 매장에 상품을 추가합니다.
     *
     * <p>요청 본문에 추가할 상품의 ID를 포함하며,
     * 해당 상품이 다른 매장에 이미 등록되어 있는 경우 예외가 발생합니다.</p>
     *
     * @param inventoryId 매장의 외부 식별자 (ULID)
     * @param req 상품 추가 요청 DTO (productId 필수)
     * @return 성공 시 본문 없는 {@code 200 OK} 응답을 반환합니다.
     * @status 200 OK
     */
    @PostMapping("/api/inventories/{inventoryId}/products")
    public ResponseEntity<Void> addProductToInventory(
            @PathVariable String inventoryId,
            @Valid @RequestBody InventoryProductRequest req
    ) {
        inventoryService.addProduct(inventoryId, req.productId());
        return ResponseEntity.ok().build(); // 필요 시 204/201로 조정
    }

    /**
     * 특정 매장에 등록된 상품 목록을 조회합니다.
     *
     * <p>각 상품은 {@link InventoryProductResponse} 형태로 반환되며,
     * 상품명, 가격, 이미지 등의 요약 정보를 포함합니다.</p>
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @return 매장 내 상품 목록
     * @status 200 OK
     */
    @GetMapping("/api/inventories/{inventoryId}/products")
    public List<InventoryProductResponse> listInventoryProducts(@PathVariable String inventoryId) {
        return inventoryService.listProducts(inventoryId);
    }

    /**
     * 매장에서 특정 상품을 제거합니다.
     *
     * <p>상품은 즉시 매장-상품 매핑 테이블에서 제거되며,
     * 본사 상품(Product) 엔티티에는 영향을 주지 않습니다.</p>
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @param productId 상품 기본키 ID
     * @return 본문 없는 {@code 204 No Content} 응답
     * @status 204 No Content
     */
    @DeleteMapping("/api/inventories/{inventoryId}/products/{productId}")
    public ResponseEntity<Void> removeInventoryProduct(
            @PathVariable String inventoryId,
            @PathVariable Long productId
    ) {
        inventoryService.removeProduct(inventoryId, productId);
        return ResponseEntity.noContent().build();
    }

    // ==========================================================
    // Product (상품) 관련 엔드포인트
    // ==========================================================

    /**
     * 신규 상품을 등록합니다.
     *
     * <p>요청 본문에 상품명, 가격, 상태, 이미지 URL 등을 포함하며,
     * {@code productId}가 비어 있을 경우 서버에서 자동 생성됩니다.</p>
     *
     * <p>상품이 정상적으로 생성되면 {@code 201 Created} 와 함께
     * 생성된 상품 정보를 반환합니다.</p>
     *
     * @param req 상품 생성 요청 DTO
     * @return 생성된 상품 정보 DTO와 Location 헤더
     * @status 201 Created
     */
    @PostMapping("/api/products")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest req) {
        Long id = productService.create(req);
        ProductResponse body = productService.get(id);
        return ResponseEntity.created(URI.create("/api/products/" + id)).body(body);
    }

    /**
     * 상품을 기본키 ID로 조회합니다.
     *
     * <p>존재하지 않는 ID를 조회할 경우 404 예외가 발생합니다.</p>
     *
     * @param id 상품 기본키 ID
     * @return 상품 상세 정보 DTO
     * @status 200 OK, 404 Not Found
     */
    @GetMapping("/api/products/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.get(id);
    }

    /**
     * 상품 목록을 페이지 단위로 조회합니다.
     *
     * <p>상품명, 가격, 상태 등의 기본 정보를 포함하며,
     * 페이지네이션(Pageable) 파라미터를 이용해
     * 페이지 번호와 크기를 지정할 수 있습니다.</p>
     *
     * @param pageable 페이지 요청 정보 (page, size, sort)
     * @return 상품 목록 페이지 객체
     * @status 200 OK
     */
    @GetMapping("/api/products")
    public Page<ProductResponse> listProducts(Pageable pageable) {
        return productService.list(pageable);
    }

    /**
     * 기존 상품 정보를 수정합니다.
     *
     * <p>요청 본문에는 수정할 필드(예: 이름, 가격, 상태 등)만 포함할 수 있으며,
     * 부분 업데이트(PATCH) 방식으로 동작합니다.</p>
     *
     * @param id 수정할 상품의 기본키 ID
     * @param req 상품 수정 요청 DTO
     * @return 본문 없는 {@code 204 No Content} 응답
     * @status 204 No Content, 404 Not Found
     */
    @PatchMapping("/api/products/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequest req) {
        productService.update(id, req);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품을 삭제(아카이브)합니다.
     *
     * <p>상품을 실제로 DB에서 삭제하지 않고
     * 상태를 {@link ProductStatus#ARCHIVED} 로 변경하여
     * 소프트 삭제 처리합니다.</p>
     *
     * @param id 상품 기본키 ID
     * @return 본문 없는 {@code 204 No Content} 응답
     * @status 204 No Content, 404 Not Found
     */
    @DeleteMapping("/api/products/{id}")
    public ResponseEntity<Void> archiveProduct(@PathVariable Long id) {
        productService.archive(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품명을 기준으로 상품을 검색합니다.
     *
     * <p>대소문자를 구분하지 않으며,
     * {@code ARCHIVED} 상태의 상품은 제외됩니다.</p>
     *
     * @param name 검색할 상품명 (부분 일치 가능)
     * @param pageable 페이지 요청 정보
     * @return 검색된 상품 목록 페이지
     * @status 200 OK
     */
    @GetMapping("/api/products/search")
    public Page<ProductResponse> searchByName(
            @RequestParam("name") String name,
            Pageable pageable
    ) {
        return productService.searchByName(name, pageable);
    }



}

package org.example.cloudpos.inventory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ProductService productService;

    // ==========================================================
    // Inventory (매장) 관련 엔드포인트
    // ==========================================================

    @Operation(
            summary = "신규 매장 생성",
            description = "ULID를 외부 식별자로 사용하는 매장을 생성합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "매장 생성 성공",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @PostMapping("/inventories")
    public ResponseEntity<String> createInventory(@Valid @RequestBody InventoryCreateRequest req) {
        String inventoryId = inventoryService.create(req);
        return ResponseEntity.created(URI.create("/api/inventories/" + inventoryId)).body(inventoryId);
    }

    @Operation(
            summary = "매장에 상품 추가",
            description = "특정 매장에 상품을 등록(연결)합니다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "상품 추가 성공",
            content = @Content
    )
    @PostMapping("/inventories/{inventoryId}/products")
    public ResponseEntity<Void> addProductToInventory(
            @PathVariable String inventoryId,
            @Valid @RequestBody InventoryProductRequest req
    ) {
        inventoryService.addProduct(inventoryId, req.productId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "매장 내 상품 목록 조회",
            description = "특정 매장에 등록된 모든 상품을 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "상품 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = InventoryProductResponse.class))
    )
    @GetMapping("/inventories/{inventoryId}/products")
    public ResponseEntity<List<InventoryProductResponse>> listInventoryProducts(@PathVariable String inventoryId) {
        return ResponseEntity.ok(inventoryService.listProducts(inventoryId));
    }

    @Operation(
            summary = "매장에서 상품 제거",
            description = "매장-상품 매핑 관계를 해제합니다. 본사 상품(Product) 엔티티에는 영향을 주지 않습니다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "상품 제거 성공",
            content = @Content
    )
    @DeleteMapping("/inventories/{inventoryId}/products/{productId}")
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

    @Operation(
            summary = "신규 상품 등록",
            description = "상품명, 가격, 상태, 이미지 URL 등을 포함하여 상품을 생성합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "상품 생성 성공",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
    )
    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest req) {
        Long id = productService.create(req);
        ProductResponse body = productService.get(id);
        return ResponseEntity.created(URI.create("/api/products/" + id)).body(body);
    }

    @Operation(
            summary = "상품 단건 조회",
            description = "상품의 기본키 ID로 상세 정보를 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "상품 조회 성공",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
    )
    @GetMapping("/products/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.get(id);
    }

    @Operation(
            summary = "상품 목록 및 검색",
            description = "상품명(name) 쿼리 파라미터가 있을 경우 부분 일치 검색, 없을 경우 전체 목록을 반환합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "상품 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
    )
    @GetMapping("/products")
    public Page<ProductResponse> listProducts(
            @RequestParam(value = "name", required = false) String name,
            Pageable pageable
    ) {
        if (name != null && !name.isBlank()) {
            return productService.searchByName(name, pageable);
        }
        return productService.list(pageable);
    }

    @Operation(
            summary = "상품 정보 수정",
            description = "상품의 이름, 가격, 상태 등을 수정합니다. PATCH 방식으로 부분 업데이트를 수행합니다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "상품 수정 성공",
            content = @Content
    )
    @PatchMapping("/products/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequest req) {
        productService.update(id, req);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "상품 아카이브(논리 삭제)",
            description = "상품을 DB에서 삭제하지 않고 상태를 ARCHIVED로 변경합니다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "상품 아카이브 성공",
            content = @Content
    )
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> archiveProduct(@PathVariable Long id) {
        productService.archive(id);
        return ResponseEntity.noContent().build();
    }
}

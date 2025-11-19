package org.example.cloudpos.inventory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.inventory.dto.InventoryCreateRequest;
import org.example.cloudpos.inventory.dto.InventoryProductResponse;
import org.example.cloudpos.inventory.service.InventoryService;
import org.example.cloudpos.product.dto.ProductCreateRequest;
import org.example.cloudpos.product.dto.ProductResponse;
import org.example.cloudpos.product.dto.ProductUpdateRequest;
import org.example.cloudpos.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 매장/상품 관리를 담당하는 복합 컨트롤러.
 * (1) inventory(매장) CRUD, (2) 매장-상품 매핑, (3) 본사 상품 CRUD 를 한번에 제공한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ProductService productService;

    // ---------- Inventory ----------

    @Operation(
            summary = "신규 매장 생성",
            description = """
                    요청 본문으로 매장 이름만 전달하면 서버에서 ULID 기반 inventoryId 를 생성합니다.
                    응답 본문은 inventoryId 문자열이고, Location 헤더는 /api/inventories/{id} 로 설정됩니다.
                    """
    )
    @ApiResponse(responseCode = "201", description = "매장 생성 성공",
            content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/inventories")
    public ResponseEntity<String> createInventory(@Valid @RequestBody InventoryCreateRequest req){

        String inventoryId = inventoryService.create(req);
        return ResponseEntity.created(URI.create("/api/inventories/" + inventoryId)).body(inventoryId);
    }

    @Operation(
            summary = "매장에 상품 등록",
            description = """
                    inventoryId 경로 변수를 기준으로 매장에 상품을 등록합니다.
                    본문에는 ProductCreateRequest 와 (선택) 이미지 파일을 멀티파트로 전달합니다.
                    """
    )
    @ApiResponse(responseCode = "201", description = "상품 등록 성공",
            content = @Content(schema = @Schema(implementation = ProductResponse.class)))
    @PostMapping(
            value = "/inventories/{inventoryId}/products",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProductResponse> createProduct(
            @PathVariable String inventoryId,
            @RequestPart("data") @Valid ProductCreateRequest req,
            @RequestPart(name = "image", required = false) MultipartFile image
    ){

        ProductResponse body = inventoryService.addProduct(inventoryId, req, image);
        return ResponseEntity
                .created(URI.create("/api/inventories/" + inventoryId + "/products/" + body.productId()))
                .body(body);
    }

    @Operation(
            summary = "매장 상품 목록 조회",
            description = "inventoryId 로 등록된 모든 상품을 InventoryProductResponse 리스트로 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = InventoryProductResponse.class)))
    @GetMapping("/inventories/{inventoryId}/products")
    public ResponseEntity<List<InventoryProductResponse>> listInventoryProducts(@PathVariable String inventoryId){

        return ResponseEntity.ok(inventoryService.listProducts(inventoryId));
    }

    @Operation(
            summary = "매장에서 상품 제거",
            description = "매장-상품 매핑만 삭제하며, Product 엔티티 자체에는 영향을 주지 않습니다."
    )
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/inventories/{inventoryId}/products/{productId}")
    public ResponseEntity<Void> removeInventoryProduct(
            @PathVariable String inventoryId,
            @PathVariable String productId
    ){

        inventoryService.removeProduct(inventoryId, productId);
        return ResponseEntity.noContent().build();
    }

    // ---------- Product ----------

    @Operation(
            summary = "상품 단건 조회",
            description = "productId(ULID) 로 ProductResponse 를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ProductResponse.class)))
    @GetMapping("/products/{productId}")
    public ProductResponse getProduct(@PathVariable String productId){

        return productService.get(productId);
    }

    @Operation(
            summary = "상품 목록 및 검색",
            description = """
                    name 파라미터가 존재하면 부분 일치 검색을 수행하고,
                    없으면 status != ARCHIVED 조건으로 전체 목록을 페이지네이션하여 반환합니다.
                    """
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ProductResponse.class)))
    @GetMapping("/products")
    public Page<ProductResponse> listProducts(
            @RequestParam(value = "name", required = false) String name,
            Pageable pageable
    ){

        if (name != null && !name.isBlank()) {
            return productService.searchByName(name, pageable);
        }
        return productService.list(pageable);
    }

    @Operation(
            summary = "상품 정보 수정",
            description = """
                    멀티파트 요청을 사용하여 JSON(ProductUpdateRequest) + 이미지 파일을 함께 전달합니다.
                    제공된 필드만 부분 업데이트하며, 이미지 파트가 있으면 기존 이미지를 교체합니다.
                    """
    )
    @ApiResponse(responseCode = "204", description = "수정 성공")
    @PatchMapping(
            value = "/products/{productId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> updateProduct(
            @PathVariable String productId,
            @RequestPart("data") ProductUpdateRequest req,
            @RequestPart(name = "image", required = false) MultipartFile image
    ){

        productService.update(productId, req, image);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "상품 아카이브",
            description = "상품을 삭제하지 않고 상태를 ARCHIVED 로 변경하여 목록/검색에서 제외합니다."
    )
    @ApiResponse(responseCode = "204", description = "아카이브 성공")
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> archiveProduct(@PathVariable String productId){

        productService.archive(productId);
        return ResponseEntity.noContent().build();
    }
}

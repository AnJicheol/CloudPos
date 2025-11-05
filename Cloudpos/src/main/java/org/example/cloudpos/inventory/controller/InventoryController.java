package org.example.cloudpos.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.inventory.dto.InventoryCreateRequest;
import org.example.cloudpos.inventory.dto.InventoryProductRequest;
import org.example.cloudpos.inventory.dto.InventoryProductResponse;
import org.example.cloudpos.inventory.service.InventoryService;
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
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * 신규 매장을 생성합니다.
     *
     * <p>매장은 ULID를 외부 식별자로 사용하며,
     * 생성 시 상품은 포함되지 않습니다.</p>
     *
     * @param req 매장 생성 요청 DTO
     * @return 생성된 매장의 ULID
     */
    @PostMapping
    public ResponseEntity<String> createInventory(@Valid @RequestBody InventoryCreateRequest req) {
        String inventoryId = inventoryService.create(req);
        return ResponseEntity.created(URI.create("/api/inventories/" + inventoryId)).body(inventoryId);
    }

    /**
     * 매장에 상품을 추가합니다.
     *
     * <p>요청 본문에 추가할 상품의 ID를 포함합니다.</p>
     *
     * @param inventoryId 매장의 외부 식별자 (ULID)
     * @param req 상품 추가 요청 DTO
     */
    @PostMapping("/{inventoryId}/products")
    public ResponseEntity<Void> addProduct(
            @PathVariable String inventoryId,
            @Valid @RequestBody InventoryProductRequest req
    ) {
        inventoryService.addProduct(inventoryId, req.productId());
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 매장에 등록된 상품 목록을 조회합니다.
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @return 상품 목록
     */
    @GetMapping("/{inventoryId}/products")
    public List<InventoryProductResponse> listProducts(@PathVariable String inventoryId) {
        return inventoryService.listProducts(inventoryId);
    }

    /**
     * 매장에서 특정 상품을 제거합니다.
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @param productId 상품 기본키 ID
     */
    @DeleteMapping("/{inventoryId}/products/{productId}")
    public ResponseEntity<Void> removeProduct(
            @PathVariable String inventoryId,
            @PathVariable Long productId
    ) {
        inventoryService.removeProduct(inventoryId, productId);
        return ResponseEntity.noContent().build();
    }
}

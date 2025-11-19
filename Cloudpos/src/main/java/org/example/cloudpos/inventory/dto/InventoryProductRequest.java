package org.example.cloudpos.inventory.dto;

import org.example.cloudpos.inventory.service.InventoryServiceImpl;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 매장(Inventory)에 상품을 추가할 때 사용하는 요청 DTO.
 *
 * <p>클라이언트가 특정 매장에 상품을 등록할 때,
 * 요청 본문에 {@code productId}를 전달합니다.</p>
 *
 * <p>해당 상품 ID는 이미 본사 상품 테이블({@code Product})에 존재해야 하며,
 * 존재하지 않거나 이미 다른 매장에 등록된 상품일 경우 예외가 발생합니다.</p>
 *
 * <h2>예시 요청(JSON)</h2>
 * <pre>{@code
 * {
 *   "productId": "PRD_01HXXXXXX"
 * }
 * }</pre>
 *
 * @param productId 추가할 상품의 식별자(비즈니스 키, ULID 문자열 등)
 *
 * @see InventoryServiceImpl#addProduct(String, String)
 * @since 1.0
 */
public record InventoryProductRequest(
        @NotBlank String productId
) {}


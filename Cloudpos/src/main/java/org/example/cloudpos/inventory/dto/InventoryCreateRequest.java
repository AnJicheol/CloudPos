package org.example.cloudpos.inventory.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 매장(Inventory) 생성 요청 DTO.
 *
 * <p>클라이언트가 신규 매장을 생성할 때 사용하는 요청 본문 형식입니다.</p>
 *
 * <p>요청에는 매장 이름만 포함되며,
 * 서버는 ULID 기반의 {@code inventoryId}를 자동 생성합니다.</p>
 *
 * <h2>예시 요청(JSON)</h2>
 * <pre>{@code
 * {
 *   "name": "강남점"
 * }
 * }</pre>
 *
 * @param name 매장명 (필수, 공백 불가)
 *
 * @see org.example.cloudpos.inventory.service.InventoryService#create(InventoryCreateRequest)
 * @since 1.0
 */
public record InventoryCreateRequest(
        @NotBlank String name
) {}

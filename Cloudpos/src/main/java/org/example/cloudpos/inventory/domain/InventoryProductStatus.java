package org.example.cloudpos.inventory.domain;

/**
 * 매장 내 개별 상품의 상태를 나타내는 열거형입니다.
 *
 * <ul>
 *   <li>{@code ACTIVE}  - 매장에서 정상 판매 중인 상품</li>
 *   <li>{@code REMOVED} - 매장에서 제거(소프트 삭제)된 상품</li>
 * </ul>
 */
public enum InventoryProductStatus {
    ACTIVE,
    REMOVED
}

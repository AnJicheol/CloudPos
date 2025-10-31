package org.example.cloudpos.product;

/**
 * 상품의 현재 상태를 나타내는 열거형(Enum)입니다.
 *
 * <p>각 상태는 판매 가능 여부나 시스템 내 노출 상태를 구분하는 데 사용됩니다.
 * <ul>
 *   <li>{@link #ACTIVE} — 판매 중이며 목록에 노출됩니다.</li>
 *   <li>{@link #INACTIVE} — 일시적으로 판매가 중단되며, 목록에는 노출되지 않습니다.</li>
 *   <li>{@link #ARCHIVED} — 더 이상 판매하지 않지만, 기록 보존을 위해 유지됩니다.</li>
 * </ul>
 * </p>
 *
 * <p>이 Enum은 {@link Product} 엔티티의 {@code status} 필드에 매핑되어
 * 상품의 상태를 관리하는 데 사용됩니다.</p>
 *
 * @author Esther
 * @since 1.0
 */
public enum ProductStatus {

    /**
     * 판매 중 상태.
     * <p>상품이 현재 활성 상태이며, 사용자에게 노출됩니다.</p>
     */
    ACTIVE,

    /**
     * 판매 중단 상태.
     * <p>상품이 일시적으로 비활성화되어 있으며, 목록에는 표시되지 않습니다.</p>
     * <p>예: 재고 부족, 임시 품절 등</p>
     */
    INACTIVE,

    /**
     * 보관 상태.
     * <p>더 이상 판매하지 않지만, 삭제하지 않고 데이터베이스에 기록을 남겨 둡니다.</p>
     * <p>예: 단종 상품, 과거 판매 기록 보존 목적</p>
     */
    ARCHIVED
}

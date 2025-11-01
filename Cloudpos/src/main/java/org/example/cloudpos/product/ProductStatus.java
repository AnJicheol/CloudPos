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

    ACTIVE,

    INACTIVE,

    ARCHIVED
}
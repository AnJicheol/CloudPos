package org.example.cloudpos.inventory.exception;

import org.example.cloudpos.inventory.service.InventoryServiceImpl;

/**
 * 매장에 동일한 상품을 중복 등록하려 할 때 발생하는 예외입니다.
 *
 * <p>이미 다른 매장 또는 동일 매장에 등록된 상품을
 * 다시 추가하려는 경우 {@link InventoryServiceImpl}
 * 에서 이 예외를 던집니다.</p>
 *
 * <p>데이터베이스의 유니크 제약조건 위반이나
 * 비즈니스 로직 검증 단계에서 발생할 수 있습니다.</p>
 *
 * <h2>예외 발생 조건</h2>
 * <ul>
 *   <li>이미 등록된 {@code (inventoryId, productId)} 조합으로 INSERT 시도</li>
 *   <li>또는 동일 {@code productId}가 다른 매장에 이미 존재하는 경우</li>
 * </ul>
 *
 * <p>이 예외는 컨트롤러 단에서 {@code 409 Conflict} 상태 코드로 변환됩니다.</p>
 *
 * @see InventoryServiceImpl#addProduct(String, Long)
 * @since 1.0
 */
public class DuplicateStoreProductException extends RuntimeException {

    /**
     * 지정된 매장 ID와 상품 ID 조합이 이미 존재할 때 예외를 생성합니다.
     *
     * @param inventoryId 매장의 외부 식별자 (ULID)
     * @param productId 중복된 상품의 ID
     */
    public DuplicateStoreProductException(String inventoryId, String productId) {
        super("이미 등록된 상품입니다. inventoryId=" + inventoryId + ", productId=" + productId);
    }

    /**
     * 지정된 매장 ID와 상품 ID 조합이 이미 존재하며,
     * 내부 원인 예외가 함께 제공되는 경우 예외를 생성합니다.
     *
     * @param inventoryId 매장의 외부 식별자 (ULID)
     * @param productId 중복된 상품의 ID
     * @param cause 내부 원인 예외 (예: DataIntegrityViolationException)
     */
    public DuplicateStoreProductException(String inventoryId, String productId, Throwable cause) {
        super("이미 등록된 상품입니다. inventoryId=" + inventoryId + ", productId=" + productId, cause);
    }
}

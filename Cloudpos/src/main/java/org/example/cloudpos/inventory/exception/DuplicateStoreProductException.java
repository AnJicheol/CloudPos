package org.example.cloudpos.inventory.exception;

/**
 * 매장에 동일한 상품을 중복 등록하려 할 때 발생하는 예외입니다.
 */
public class DuplicateStoreProductException extends RuntimeException {
    public DuplicateStoreProductException(String inventoryId, Long productId) {
        super("이미 등록된 상품입니다. inventoryId=" + inventoryId + ", productId=" + productId);
    }

    public DuplicateStoreProductException(String inventoryId, Long productId, Throwable cause) {
        super("이미 등록된 상품입니다. inventoryId=" + inventoryId + ", productId=" + productId, cause);
    }
}

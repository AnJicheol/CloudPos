package org.example.cloudpos.product.exception;

/**
 * 상품 생성 시 {@code productId}가 중복되는 경우 발생하는 예외입니다.
 *
 * <p>클라이언트가 상품 식별자(productId)를 직접 전달했을 때,
 * 이미 동일한 productId가 데이터베이스에 존재하면 이 예외가 던져집니다.</p>
 *
 * <p>자동 생성되는 productId의 경우 중복 가능성이 매우 낮지만,
 * 수동 입력을 허용하는 정책에서는 반드시 중복 검증이 필요합니다.</p>
 *
 * <pre>
 * throw new DuplicateProductIdException("P-2025-ABCD1234");
 * </pre>
 *
 * @author Esther
 * @since 1.0
 */
public class DuplicateProductIdException extends RuntimeException {
    public DuplicateProductIdException(String productId) {
        super("Duplicated productId: " + productId);
    }
}

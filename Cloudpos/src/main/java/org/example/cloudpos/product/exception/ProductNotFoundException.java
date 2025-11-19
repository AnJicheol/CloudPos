package org.example.cloudpos.product.exception;

/**
 * 상품을 조회하거나 삭제하려고 할 때,
 * 해당 상품이 존재하지 않는 경우 발생하는 예외입니다.
 *
 * <p>일반적으로 다음 상황에서 발생할 수 있습니다:</p>
 * <ul>
 *     <li>요청한 상품 ID(Long)가 DB에 존재하지 않을 때</li>
 *     <li>ULID/String 기반의 productId가 존재하지 않을 때</li>
 *     <li>이미 삭제(ARCHIVED)된 상품을 조회할 때</li>
 *     <li>다른 도메인(예: 재고/주문/장바구니)에서 잘못된 상품 ID로 연동 요청할 때</li>
 * </ul>
 *
 * <p>컨트롤러 계층에서는 {@code 404 Not Found} 응답으로 변환되어 내려가며,
 * {@link ProductExceptionHandler} 에 의해 처리됩니다.</p>
 *
 * <pre>
 * throw new ProductNotFoundException(7L);
 * // → "Product not found: 7"
 * throw new ProductNotFoundException("01HFJ78R9QFZNBKP6CZ2FZ2ZQ1");
 * // → "Product not found: 01HFJ78R9QFZNBKP6CZ2FZ2ZQ1"
 * </pre>
 *
 * @author Esther
 * @since 1.0
 */
public class ProductNotFoundException extends RuntimeException {

    /** DB 기본키(Long) 기준 예외 */
    public ProductNotFoundException(Long id) {
        super("Product not found: " + id);
    }

    /** 비즈니스 식별자(String, 예: ULID) 기준 예외 */
    public ProductNotFoundException(String productId) {
        super("Product not found: " + productId);
    }
}

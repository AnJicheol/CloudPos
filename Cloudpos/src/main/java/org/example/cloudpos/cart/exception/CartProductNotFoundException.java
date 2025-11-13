package org.example.cloudpos.cart.exception;

/**
 * <h2>CartProductNotFoundException</h2>
 *
 * 장바구니(Cart)에서 요청한 상품이 존재하지 않을 때 발생하는 예외입니다.
 *
 * <p>예: 존재하지 않는 상품 ID로 상품 정보를 조회하거나,
 * 삭제된 상품을 장바구니에 추가하려는 경우 등</p>
 */
public class CartProductNotFoundException extends RuntimeException {
    public CartProductNotFoundException(String productId) {
        super("Product not found for id=" + productId);
    }
}
package org.example.cloudpos.cart.exception;
/**
 * 장바구니 TTL이 만료되었거나 Redis에서 더 이상 존재하지 않을 때 발생하는 예외.
 *
 * <p>
 * - 클라이언트 요청 시 이미 만료된 장바구니(cartId)에 접근하면 이 예외가 던져진다.<br>
 * - ControllerAdvice 등에서 HTTP 410(Gone) 또는 404(Not Found)로 매핑한다.
 * </p>
 */
public class CartExpiredException extends RuntimeException {

    /**
     * 만료된 장바구니 식별자를 포함한 예외 메시지를 생성한다.
     *
     * @param cartId 만료된 장바구니의 ID
     */
    public CartExpiredException(String cartId) {
        super("Cart session expired or not found: cartId=" + cartId);
    }
}

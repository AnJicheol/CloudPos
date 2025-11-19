package org.example.cloudpos.cart.application;

import org.example.cloudpos.cart.dto.CartItemResponse;
import org.example.cloudpos.cart.service.CartServiceImpl;

import java.util.List;
/**

 * 장바구니의 결제 프로세스 전이를 정의하는 유스케이스 인터페이스입니다.
 *
 * <p><b>기능 개요</b></p>
 * <ul>
 * <li>{@code beginCheckout} — 결제를 시작하며 상태를 {@code CHECKOUT_PENDING}으로 전이</li>
 * <li>{@code paymentSuccess} — 결제가 성공하면 {@code CLOSED}로 전이하고 필요 시 장바구니를 비움</li>
 * <li>{@code cancelCheckout} — 결제 취소 또는 실패 시 {@code IN_PROGRESS}로 복귀</li>
 * </ul>
 *
 * <p><b>비고</b><br>
 * 실제 비즈니스 로직은 {@link CartServiceImpl}를 이용하는
 * 구현체({@code CartCheckoutService})에서 처리됩니다.
 * </p>

 */

public interface CartCheckoutUseCase {
    /** 결제 시작: CHECKOUT_PENDING 으로 전이 */
    List<CartItemResponse> beginCheckout(String cartId);

    /** 결제 성공: CLOSED 로 전이 (정책에 따라 clear 가능) */
    void paymentSuccess(String cartId);

    /** 결제 취소/실패: IN_PROGRESS 로 복귀 */
    void cancelCheckout(String cartId);
}
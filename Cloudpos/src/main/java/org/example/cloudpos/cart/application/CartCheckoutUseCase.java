package org.example.cloudpos.cart.application;

import java.util.List;

public interface CartCheckoutUseCase {

    /** 결제 시작: CHECKOUT_PENDING 으로 전이 */
    void beginCheckout(String cartId);

    /** 결제 성공: CLOSED 로 전이 (정책에 따라 clear 가능) */
    void paymentSuccess(String cartId);

    /** 결제 취소/실패: IN_PROGRESS 로 복귀 */
    void cancelCheckout(String cartId);
}
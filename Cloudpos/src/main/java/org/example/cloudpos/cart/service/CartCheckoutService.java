package org.example.cloudpos.cart.service;

import org.example.cloudpos.cart.domain.CartState;

/**
 * 장바구니 결제 진행/완료/취소를 담당하는 서비스 인터페이스.
 */
public interface CartCheckoutService {

    CartState getState(String cartId);

    void beginCheckout(String cartId);

    void paymentSuccess(String cartId);

    void cancelCheckout(String cartId);

    void clear(String cartId);
}

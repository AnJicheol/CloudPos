package org.example.cloudpos.cart.listener;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.cart.exception.InvalidCartStateException;
import org.example.cloudpos.cart.service.CartCheckoutServiceImpl;
import org.example.cloudpos.cart.service.CartServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <h2>OrderCartStateListenerImpl</h2>
 *
 * {@link OrderCartStateListener}의 구현체로,
 * 장바구니(Cart)의 상태 변화를 감지하여 {@link CartCheckoutServiceImpl}를 통해
 * 적절한 상태 전이 및 아이템 조회를 수행합니다.
 *
 * <p>결제 시작, 결제 성공, 결제 취소 등의 이벤트를 처리하며
 * 장바구니 상태가 올바르지 않을 경우 {@link InvalidCartStateException}을 발생시킵니다.</p>
 */
@Component
@RequiredArgsConstructor
public class OrderCartStateListenerImpl implements OrderCartStateListener {

    private final CartCheckoutServiceImpl cartCheckoutService;
    private final CartServiceImpl cartService;

    @Override
    public void onClose(String cartId) {
        cartCheckoutService.paymentSuccess(cartId);
    }

    @Override
    public void onOpen(String cartId) {
        cartCheckoutService.cancelCheckout(cartId);
    }

    @Override
    public List<CartItemDto> onPayment(String cartId) {
        if (cartCheckoutService.getState(cartId) != CartState.CHECKOUT_PENDING) {
            throw new InvalidCartStateException("장바구니가 결제 가능한 상태가 아닙니다.");

        }
        cartCheckoutService.beginCheckout(cartId);
        return cartService.getAll(cartId);
    }




}

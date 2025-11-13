package org.example.cloudpos.cart.listener;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.cart.exception.InvalidCartStateException;
import org.example.cloudpos.cart.service.CartService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderCartStateListenerImpl implements OrderCartStateListener {

    private final CartService cartService;

    @Override
    public void onClose(String cartId) {
        cartService.paymentSuccess(cartId);
    }

    @Override
    public void onOpen(String cartId) {
        cartService.cancelCheckout(cartId);
    }

    @Override
    public List<CartItemDto> onPayment(String cartId) {
        if (cartService.getState(cartId) != CartState.CHECKOUT_PENDING) {
            throw new InvalidCartStateException("장바구니가 결제 가능한 상태가 아닙니다.");

        }
        cartService.beginCheckout(cartId);
        return cartService.getAll(cartId);
    }




}

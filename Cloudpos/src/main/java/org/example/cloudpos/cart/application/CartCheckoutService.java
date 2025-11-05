package org.example.cloudpos.cart.application;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.cart.service.CartService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartCheckoutService implements CartCheckoutUseCase {

    private final CartService cartService;

    @Override
    public List<CartItemDto>  beginCheckout(String cartId) {
        if (cartService.getState(cartId) != CartState.CHECKOUT_PENDING) {
            cartService.beginCheckout(cartId);
        }
        return cartService.getAll(cartId);
    }

    @Override
    public void paymentSuccess(String cartId) {
        cartService.paymentSuccess(cartId);
    }

    @Override
    public void cancelCheckout(String cartId) {
        cartService.cancelCheckout(cartId);
    }
}
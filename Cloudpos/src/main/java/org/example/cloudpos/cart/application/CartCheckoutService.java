package org.example.cloudpos.cart.application;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.cart.exception.CartExpiredException;
import org.example.cloudpos.cart.exception.InvalidCartStateException;
import org.example.cloudpos.cart.service.CartService;
import org.springframework.stereotype.Service;

import java.util.List;
/**

 * 장바구니 결제 프로세스를 처리하는 서비스입니다.
 *
 * <p>결제 시작, 성공, 취소에 따른 상태 전이를 {@link CartService}를 통해 수행합니다.</p>

 */

@Service
@RequiredArgsConstructor
public class CartCheckoutService implements CartCheckoutUseCase {

    private final CartService cartService;

    @Override
    public List<CartItemDto>  beginCheckout(String cartId) {
        if (cartService.getState(cartId) != CartState.CHECKOUT_PENDING) {
            throw new InvalidCartStateException("장바구니가 결제 가능한 상태가 아닙니다.");
        }
        cartService.beginCheckout(cartId);
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
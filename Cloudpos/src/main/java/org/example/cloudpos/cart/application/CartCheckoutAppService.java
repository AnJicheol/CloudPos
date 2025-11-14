package org.example.cloudpos.cart.application;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.cart.service.CartService;
import org.example.cloudpos.cart.service.CartCheckoutService;
import org.springframework.stereotype.Service;

import java.util.List;
/**

 * 장바구니 결제 프로세스를 처리하는 서비스입니다.
 *
 * <p>결제 시작, 성공, 취소에 따른 상태 전이를 {@link CartCheckoutService}를 통해 수행합니다.</p>

 */

@Service
@RequiredArgsConstructor
public class CartCheckoutAppService implements CartCheckoutUseCase {

    private final CartCheckoutService cartCheckoutService;
    private final CartService cartService;

    @Override
    public List<CartItemDto>  beginCheckout(String cartId) {
        if (cartCheckoutService.getState(cartId) != CartState.CHECKOUT_PENDING) {
            cartCheckoutService.beginCheckout(cartId);
        }
        return cartService.getAll(cartId);
    }

    @Override
    public void paymentSuccess(String cartId) {
        cartCheckoutService.paymentSuccess(cartId);
    }

    @Override
    public void cancelCheckout(String cartId) {
        cartCheckoutService.cancelCheckout(cartId);
    }
}
package org.example.cloudpos.order.api;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.application.CartCheckoutUseCase;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.order.repository.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CartStateHandlerApiImpl implements CartStateHandlerApi {
    private final OrderRepository orderRepository;
    private final CartCheckoutUseCase cartCheckoutUseCase;


    @Override
    @Transactional(readOnly = true)
    public void stateOpen(String orderId) {
        cartCheckoutUseCase.cancelCheckout(orderRepository.findCartIdByOrderId(orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public void stateClose(String orderId) {
        cartCheckoutUseCase.paymentSuccess(orderRepository.findCartIdByOrderId(orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemDto> statePayment(String orderId) {
        return cartCheckoutUseCase.beginCheckout(orderRepository.findCartIdByOrderId(orderId));
    }
}

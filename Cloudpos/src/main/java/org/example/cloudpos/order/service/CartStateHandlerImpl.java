package org.example.cloudpos.order.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.application.CartCheckoutUseCase;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.order.domain.Order;
import org.example.cloudpos.order.repository.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CartStateHandlerImpl implements CartStateHandler{
    private final OrderRepository orderRepository;
    private final CartCheckoutUseCase cartCheckoutUseCase;

    @Override
    @Transactional(readOnly = true)
    public void stateOpen(String orderId) {
        cartCheckoutUseCase.cancelCheckout(orderRepository.findCartIdByOrderId(orderId));
    }

    @Override
    public void stateClose(String orderId) {
        cartCheckoutUseCase.paymentSuccess(orderRepository.findCartIdByOrderId(orderId));
    }

    @Override
    public List<Order> statePayment(String orderId) {
        List<CartItemDto> dtos = cartCheckoutUseCase.beginCheckout(orderRepository.findCartIdByOrderId(orderId));

        return List.of();
    }
}

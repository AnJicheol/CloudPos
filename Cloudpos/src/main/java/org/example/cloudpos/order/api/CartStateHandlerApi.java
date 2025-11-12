package org.example.cloudpos.order.api;

import org.example.cloudpos.cart.dto.CartItemDto;

import java.util.List;

public interface CartStateHandlerApi {
    void stateOpen(String orderId);
    void stateClose(String orderId);
    List<CartItemDto> statePayment(String orderId);
}

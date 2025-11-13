package org.example.cloudpos.cart.listener;

import org.example.cloudpos.cart.dto.CartItemDto;

import java.util.List;

public interface OrderCartStateListener {

    void onOpen(String cartId);

    void onClose(String cartId);

    List<CartItemDto> onPayment(String CartId);

}

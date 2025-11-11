package org.example.cloudpos.order.service;

import org.example.cloudpos.order.domain.Order;

public interface OrderService {
    Order startPayment(String cartId);
    Order getOrderById(String orderId);
}

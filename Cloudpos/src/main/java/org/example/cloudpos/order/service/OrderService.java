package org.example.cloudpos.order.service;

import org.example.cloudpos.order.domain.Order;
import org.example.cloudpos.order.dto.OrderResponse;

public interface OrderService {
    OrderResponse startPayment(String cartId);
    Order getOrderById(String orderId);
}

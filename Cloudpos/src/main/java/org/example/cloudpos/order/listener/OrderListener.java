package org.example.cloudpos.order.listener;

import org.example.cloudpos.order.domain.Order;

public interface OrderListener {
    Order getOrderById(String orderId);
}

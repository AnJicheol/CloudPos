package org.example.cloudpos.payment.api;

import org.example.cloudpos.order.domain.Order;


public interface OrderApi {
    Order getOrder(String orderId);
}

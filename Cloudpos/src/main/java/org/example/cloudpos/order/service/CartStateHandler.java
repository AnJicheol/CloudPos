package org.example.cloudpos.order.service;

import org.example.cloudpos.order.domain.Order;

import java.util.List;

public interface CartStateHandler {
    void stateOpen(String orderId);
    void stateClose(String orderId);
    List<Order> statePayment(String orderId);
}

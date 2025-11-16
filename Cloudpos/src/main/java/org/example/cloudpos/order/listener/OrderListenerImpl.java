package org.example.cloudpos.order.listener;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.order.domain.Order;
import org.example.cloudpos.order.service.OrderService;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class OrderListenerImpl implements OrderListener{
    private OrderService orderService;


    @Override
    public Order getOrderById(String orderId) {
        return orderService.getOrderById(orderId);
    }
}

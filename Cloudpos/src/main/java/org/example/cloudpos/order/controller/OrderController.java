package org.example.cloudpos.order.controller;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.order.domain.Order;
import org.example.cloudpos.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/start-payment/{cartId}")
    public ResponseEntity<Order> startPayment(@PathVariable String cartId) {
        Order order = orderService.startPayment(cartId);
        return ResponseEntity.ok(order);
    }
}
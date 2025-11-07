package org.example.cloudpos.order.controller;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.order.service.PaymentResultPort;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final PaymentResultPort orderPaymentHub;


}

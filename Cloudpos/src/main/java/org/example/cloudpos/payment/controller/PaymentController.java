package org.example.cloudpos.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.example.cloudpos.order.domain.Order;
import org.example.cloudpos.order.listener.OrderListener;
import org.example.cloudpos.payment.dto.PaymentRequest;
import org.example.cloudpos.payment.dto.PaymentResponse;
import org.example.cloudpos.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <h2>PaymentController</h2>
 *
 * 내부 결제 정보 생성 및 조회를 담당하는 REST 컨트롤러입니다.
 * (토스 승인/취소는 TossPaymentController에서 처리)
 */

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderListener orderListener;

    //결제 생성
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        //주문조회
        Order order = orderListener.getOrderById(request.getOrderId());

        PaymentResponse response = paymentService.createPayment(order, request);
        return ResponseEntity.ok(response);
    }

    //주문아이디로 결제 정보 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String orderId) {
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }
}

package org.example.cloudpos.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudpos.order.Order;
import org.example.cloudpos.payment.domain.Payment;
import org.example.cloudpos.payment.domain.PaymentMethod;
import org.example.cloudpos.payment.domain.PaymentStatus;
import org.example.cloudpos.payment.dto.PaymentRequest;
import org.example.cloudpos.payment.dto.PaymentResponse;
import org.example.cloudpos.payment.repository.PaymentMethodRepository;
import org.example.cloudpos.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <h2>PaymentService</h2>
 *
 * 내부 결제 생성/조회 로직을 담당하는 서비스 클래스입니다.
 * - 결제 준비(생성)
 * - 결제 조회
 *
 * 실제 결제 승인/취소 로직은 TossPaymentService에서 처리합니다.
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    //결제 생성, 주문생성시 함께 호출되어 Payment엔티티 생성
    @Transactional
    public PaymentResponse createPayment(Order order, PaymentRequest request) {
        PaymentMethod method = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 결제 수단입니다. id=" + request.getPaymentMethodId()));

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(method)
                .paymentStatus(PaymentStatus.BEFORE_PAYMENT)
                .amountFinal(order.getTotalAmount()) // 주문 총액을 받아와야하는데 의논필요
                .build();

        paymentRepository.save(payment);
        log.info("[결제 생성 완료] orderId={}, method={}, amount={}",
                order.getOrderId(), method.getName(), payment.getAmountFinal());

        return PaymentResponse.from(payment);
    }

    // 주문아이디로 결제조회
    public PaymentResponse getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문의 결제 정보를 찾을 수 없습니다. orderId=" + orderId));

        return PaymentResponse.from(payment);
    }
}

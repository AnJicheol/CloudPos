package org.example.cloudpos.payment.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.cloudpos.payment.domain.Payment;
import org.example.cloudpos.payment.domain.PaymentStatus;

import java.time.LocalDateTime;


/**
 * <h2>PaymentResponse</h2>
 *
 * 결제 생성 또는 조회 후 클라이언트/주문 서비스로 반환되는 DTO입니다.
 */

@Getter
@Builder
public class PaymentResponse {

    private Long id;
    private String orderId;
    private int amountFinal;
    private String methodName;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .amountFinal(payment.getAmountFinal())
                .methodName(payment.getPaymentMethod().getName())
                .status(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}

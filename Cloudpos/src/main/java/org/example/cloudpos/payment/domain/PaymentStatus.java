package org.example.cloudpos.payment.domain;

public enum PaymentStatus {
    BEFORE_PAYMENT, // 결제 전 상태
    COMPLETED,      // 결제 완료
    FAILED,         // 결제 실패
    CANCELED        // 결제 취소
}

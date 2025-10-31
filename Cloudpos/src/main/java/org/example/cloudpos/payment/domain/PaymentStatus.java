package org.example.cloudpos.payment.domain;

/**
 * PaymentStatus
 *
 * 결제 상태를 정의하는 Enum 클래스.
 * 결제 진행 흐름을 명확히 표현하기 위해 사용됨.
 *
 * BEFORE_PAYMENT : 결제 전 상태 (주문 생성 후 결제 대기)
 * COMPLETED      : 결제 성공 및 완료된 상태
 * FAILED         : 결제 실패 (예: 카드 승인 거절 등)
 * CANCELED       : 결제 취소 (사용자 또는 관리자에 의한 취소)
 */
public enum PaymentStatus {
    BEFORE_PAYMENT, // 결제 전 상태
    COMPLETED,      // 결제 완료
    FAILED,         // 결제 실패
    CANCELED        // 결제 취소
}

package org.example.cloudpos.payment.domain;
/**
 * PaymentMethod
 *
 * 결제 수단을 정의하는 Enum 클래스.
 * 결제 시 어떤 방식으로 처리되었는지를 명시함.
 *
 * CASH : 현금 결제
 * CARD : 카드 결제
 */
public enum PaymentMethod {
    CASH,CARD
}

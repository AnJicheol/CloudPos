package org.example.cloudpos.payment.domain;
/**
 * DiscountType
 *
 * 할인 정책의 유형을 정의하는 Enum 클래스.
 * 정책별로 할인 금액 계산 로직이 달라짐.
 *
 * PERCENTAGE  : 퍼센트(%) 단위 할인 (예: 10%)
 * FIXED_AMOUNT: 고정 금액 단위 할인 (예: 5000원)
 */
public enum DiscountType {
    PERCENTAGE, FIXED_AMOUNT // 할인비율 or 고정가 할인
}

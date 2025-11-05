package org.example.cloudpos.payment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 결제 할인 내역(PaymentDiscount) 엔티티
 *
 * 결제(Payment)에 적용된 할인 정책(DiscountPolicy)과
 * 실제 할인 금액을 기록하는 엔티티입니다.
 *
 * 예: 결제 A에 '여름 세일 10%' 정책이 적용되어 3,000원이 할인된 경우,
 *     해당 정보를 저장합니다.
 *
 * 주요 역할:
 *  - 결제와 할인 정책의 연결 (다대일 관계)
 *  - 실제 적용된 할인 금액 저장
 *  - 생성 일시 자동 기록
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment_discount")
public class PaymentDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment; // 결제 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_policy_id")
    private DiscountPolicy discountPolicy; // 할인 정책 참조

    private int discountAmount; // 실제 할인 금액
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }


}

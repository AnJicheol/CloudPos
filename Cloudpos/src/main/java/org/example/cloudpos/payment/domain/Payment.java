package org.example.cloudpos.payment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 결제(Payment) 엔티티
 *
 * 주문에 대한 결제 정보를 관리하는 도메인 클래스입니다.
 * 실제 결제 금액, 결제 수단, 상태 등을 저장하며,
 * 추후 Order 엔티티와 연관관계로 연결될 예정입니다.
 *
 * 주요 역할:
 *  - 결제 내역 저장 (결제 수단, 상태, 금액 등)
 *  - 생성/수정 시간 자동 관리
 *  - 주문(Order)와의 관계 매핑 기반 데이터 추적 예정
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId; // 주문과 관련된 엔티티로 추후에 연관관계 매핑예정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "amount_final")
    private int amountFinal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Payment(Long orderId, PaymentMethod paymentMethod, PaymentStatus paymentStatus, int amountFinal) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.amountFinal = amountFinal;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}

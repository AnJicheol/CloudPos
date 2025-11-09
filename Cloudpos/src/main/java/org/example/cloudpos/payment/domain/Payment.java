package org.example.cloudpos.payment.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.cloudpos.order.Order;
import java.time.LocalDateTime;
import java.util.UUID;


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

    @Column(name = "payment_id", nullable = false, unique = true)
    private String paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
    private Order order;

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
    public Payment(Order order, PaymentMethod paymentMethod, PaymentStatus paymentStatus, int amountFinal) {
        this.order = order;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.amountFinal = amountFinal;
    }

    @PrePersist
    public void prePersist() {
        // paymentId가 비어 있으면 자동 생성
        if (this.paymentId == null) {
            this.paymentId = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 26); // 길이 26자 제한
        }

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    //결제 비즈니스 메서드

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void updateStatus(PaymentStatus newStatus) {
        this.paymentStatus = newStatus;
    }

}

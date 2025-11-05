package org.example.cloudpos.payment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * TossPayments 결제 정보 엔티티
 *
 * 토스페이먼츠 API를 통해 승인된 결제 정보를 저장합니다.
 * 내부 Payment 엔티티와 1:1로 연결되어 실제 결제 결과를 기록합니다.
 *
 * 예시:
 *  - paymentKey: 토스 서버가 발급한 고유 결제 키
 *  - method: 카드, 카카오페이, 토스페이, 계좌이체 등
 *  - status: READY, DONE, CANCELED, PARTIAL_CANCELED 등
 *  - totalAmount: 총 결제 금액 (원 단위)
 *  - approvedAt: 결제 승인 시각
 */

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "toss_payment")
public class TossPayment {

    @Id
    @Column(name = "payment_key", nullable = false, unique = true)
    private String paymentKey; //토스에서 발급한 고유 결제 키

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id" , nullable = false)
    private Payment payment;

    @Column(name = "total_amount", nullable = false)
    private long totalAmount; // 총 결제 금액

    @Column(nullable = false)
    private String method; // 결제수단(무엇으로 결제를 했는가)

    @Column(nullable = false)
    private String status; // 결제상태

    @Column(name = "requested_at")
    private LocalDateTime requestedAt; // 결제 요청 시각

    @Column(name = "approved_at")
    private LocalDateTime approvedAt; // 결제 승인 시각

    @Column(name = "failure_reason")
    private String failureReason; // 결제 실패 사유(토스페이는 결제실패시 이유를 알려줌)

    @Column(name = "is_cancelable")
    private boolean isCancelable; // 결제 취소 가능여부

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 결제 생성 시각

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}

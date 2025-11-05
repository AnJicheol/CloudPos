package org.example.cloudpos.payment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * 할인 정책(DiscountPolicy) 엔티티
 *
 * 결제 시스템에서 적용되는 할인 정책 정보를 저장하는 도메인 클래스입니다.
 * 예: '여름 정기 세일 10%' 또는 '신규 가입자 5000원 할인' 등의 정책
 *
 * 주요 기능:
 *  - 할인 유형(퍼센트 / 금액)
 *  - 할인 값
 *  - 정책 활성 여부
 *  - 유효 기간(시작일 ~ 종료일)
 *  - 생성 / 수정 일자 자동 관리
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "discount_policy")
public class DiscountPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // 할인 이름

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DiscountType discountType; // 퍼센트 or 금액

    private int value; // 할인 값

    @Column(name = "is_active")
    private Boolean isActive;  // 정책 활성 여부

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public DiscountPolicy(String name, DiscountType discountType, int value, Boolean isActive,
                          LocalDateTime validFrom, LocalDateTime validTo) {
        this.name = name;
        this.discountType = discountType;
        this.value = value;
        this.isActive = isActive;
        this.validFrom = validFrom;
        this.validTo = validTo;
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

package org.example.cloudpos.payment.dto;

/**
 * <h2>DiscountPolicyResponse</h2>
 *
 * 할인 정책 정보를 클라이언트에게 반환하기 위한 응답 DTO입니다.
 * 엔티티(DiscountPolicy)를 외부로 직접 노출하지 않고, 필요한 데이터만 전달합니다.
 *
 * 주요 필드:
 *  - id, name, discountType, value
 *  - isActive, validFrom, validTo
 *  - createdAt, updatedAt
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.cloudpos.payment.domain.DiscountPolicy;
import org.example.cloudpos.payment.domain.DiscountType;


import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class DiscountPolicyResponse {

    private Long id;
    private String name;
    private DiscountType discountType;
    private int value;
    private Boolean isActive;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DiscountPolicyResponse from(DiscountPolicy policy) {
        return DiscountPolicyResponse.builder()
                .id(policy.getId())
                .name(policy.getName())
                .discountType(policy.getDiscountType())
                .value(policy.getValue())
                .isActive(policy.getIsActive())
                .validFrom(policy.getValidFrom())
                .validTo(policy.getValidTo())
                .createdAt(policy.getCreatedAt())
                .updatedAt(policy.getUpdatedAt())
                .build();
    }
}

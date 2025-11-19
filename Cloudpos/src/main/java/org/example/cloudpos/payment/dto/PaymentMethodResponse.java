package org.example.cloudpos.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.cloudpos.payment.domain.PaymentMethod;

import java.time.LocalDateTime;

/**
 * <h2>PaymentMethodResponse</h2>
 *
 * 결제 수단 정보를 프론트엔드로 반환하는 DTO
 */

@Getter
@Builder
@AllArgsConstructor
public class PaymentMethodResponse {

    private Long id;
    private String code;
    private String name;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 엔티티 → DTO 변환
    public static PaymentMethodResponse from(PaymentMethod entity) {
        return PaymentMethodResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

}

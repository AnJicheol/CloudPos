package org.example.cloudpos.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.cloudpos.payment.domain.DiscountType;


import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class DiscountPolicyRequest {

    private String name;
    private DiscountType discountType;
    private int value;
    private Boolean isActive;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;


}

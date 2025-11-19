package org.example.cloudpos.discount.dto.owner.update;

import java.time.LocalDateTime;

public record DiscountUpdateRequest(
        String name,
        Integer amount,
        LocalDateTime discountStart,
        LocalDateTime discountEnd
)
{ }

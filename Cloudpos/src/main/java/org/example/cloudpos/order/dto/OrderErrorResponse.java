package org.example.cloudpos.order.dto;

public record OrderErrorResponse(
        String code,
        String message
) {}

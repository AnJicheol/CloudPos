package org.example.cloudpos.order.domain;

 public enum OrderStatus {
    CREATED,     // 주문 생성됨
    PAID,        // 결제 완료
    CANCELLED,   // 주문 취소됨
    DELIVERED    // 배송 완료
}
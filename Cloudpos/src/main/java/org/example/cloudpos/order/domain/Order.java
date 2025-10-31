package org.example.cloudpos.order.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 엔티티.
 * <p>
 * 주문의 생성, 결제시각, 상태 등을 보관한다.
 * 비즈니스 로직은 Service 계층에서 처리하는 것을 기본으로 하되,
 * 편의상 항목 추가(addItem) 같은 도메인 메서드를 제공한다.
 */
@Entity
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="order_id",nullable = false)
    private Long orderId;

    /** 주문 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private OrderStatus status;

    /** 주문 생성 시각 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    /** 결제 완료 시각 */
    @Column(name="paid_at")
    private LocalDateTime paidAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    // === 편의 메서드 ===
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    // === 정적 생성 ===
    public static Order create(OrderStatus status) {
        Order o = new Order();
        o.status = status;
        o.createdAt = LocalDateTime.now();
        return o;
    }
}

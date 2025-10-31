package org.example.cloudpos.order.domain;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * 주문에 포함된 개별 상품 정보를 나타내는 엔티티.
 * <p>
 * 각 OrderItem은 하나의 주문(Order)에 속하며,
 * 주문 시점의 상품ID와 가격, 수량을 기록한다.
 * </p>
 */

@Entity
@Table(name = "orderitem")
@Getter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    /** 주문 시점의 상품 식별자 */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** 주문 수량 */
    @Column(nullable = false)
    private int quantity;

    /** 주문 당시의 단가 */
    @Column(name = "price_at_purchase", nullable = false)
    private int priceAtPurchase;

}
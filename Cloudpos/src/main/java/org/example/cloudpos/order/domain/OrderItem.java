package org.example.cloudpos.order.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;


/**
 * 주문에 포함된 개별 상품(Line Item)을 표현하는 엔티티입니다.
 * <p>
 * - {@code orderItemId} 는 외부 노출/식별용 비즈니스 키입니다.<br>
 * - {@code productId} 는 실제 상품을 식별하기 위한 코드입니다.<br>
 * - {@code price} 는 주문 시점의 금액을 스냅샷으로 보관합니다.<br>
 * - {@code paymentMethod} 는 현재 단계에서 결제 수단을 함께 받기 위한 임시 필드로,
 *   이후 결제/중간 테이블로 분리·통합될 예정입니다.
 * </p>
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "product_Id", length = 26, nullable = false)
    private String productId;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "payment_method", length = 20)
    private String paymentMethod;
}
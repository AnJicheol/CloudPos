package org.example.cloudpos.order.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;


/**
 * 주문에 포함된 개별 상품(Line Item)을 표현하는 엔티티입니다.
 *
 * <p>주문 시점의 상품, 수량, 단가 정보를 스냅샷 형태로 보관하며
 * 상품 가격이 이후에 변경되더라도 주문 당시의 금액 정보를 유지하기 위한 용도로 사용됩니다.</p>
 *
 * <ul>
 *     <li>{@code id} : 데이터베이스 내부용 기본 키</li>
 *     <li>{@code order} : 이 항목이 소속된 주문</li>
 *     <li>{@code productId} : 실제 상품을 식별하기 위한 상품 ID</li>
 *     <li>{@code quantity} : 주문 수량</li>
 *     <li>{@code price} : 주문 시점의 단가 스냅샷</li>
 * </ul>
 */
@Entity
@Getter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "order_fk",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Order order;

    @Column(name = "product_id", length = 26, nullable = false)
    private String productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price",nullable = false)
    private Integer price;

    public OrderItem(Order order, String productId, Integer quantity, Integer price) {
        this.order = order;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }
}
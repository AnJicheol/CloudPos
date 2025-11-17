package org.example.cloudpos.order.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;



/**
 * 고객의 주문을 표현하는 엔티티입니다.
 *
 * <p>
 * 주문 시점의 장바구니, 총 결제 금액, 결제 시각, 결제 수단 등을 보관하며<br>
 * 한 번 확정된 결제 금액이 이후에 변경되지 않도록 도메인 규칙을 강제합니다.
 * </p>
 *
 * <ul>
 *     <li>{@code id} : 데이터베이스 내부용 기본 키</li>
 *     <li>{@code orderId} : 외부 노출 및 다른 모듈/시스템 연동에 사용하는 문자열 주문 ID (예: ULID)</li>
 *     <li>{@code cartId} : 이 주문이 생성될 당시 기준이 된 장바구니 식별자</li>
 *     <li>{@code totalAmount} : 주문 시점에 확정된 총 결제 금액</li>
 *     <li>{@code paidAt} : 결제가 발생한 시각</li>
 *     <li>{@code paymentMethod} : 결제 수단 (카드/현금 등), 할인 정책 및 정산 로직의 기준이 됨</li>
 * </ul>
 *
 * <p>
 * 비즈니스 규칙:
 * <ul>
 *     <li>{@link #applyTotalAmount(int)} 는 <b>단 한 번만</b> 호출될 수 있으며,
 *         이미 금액이 설정된 주문에 대해 다시 호출하면 예외가 발생합니다.</li>
 * </ul>
 * </p>
 */
@Getter
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", length = 26, nullable = false, unique = true)
    private String orderId;

    @Column(name = "cart_id", length = 26, nullable = false)
    private String cartId;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;


    public Order(String orderId, String cartId, LocalDateTime paidAt) {
        this.orderId = orderId;
        this.cartId = cartId;
        this.totalAmount = 0;
        this.paidAt = paidAt;
    }

    public void applyTotalAmount(int amount) {
        if (this.totalAmount != 0) {
            throw new IllegalStateException("이미 금액이 확정된 주문입니다.");
        }
        this.totalAmount = amount;
    }

}

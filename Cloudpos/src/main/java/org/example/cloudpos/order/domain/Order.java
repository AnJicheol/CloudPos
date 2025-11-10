package org.example.cloudpos.order.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Getter
@Entity
@NoArgsConstructor
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

    public Order(LocalDateTime paidAt, String cartId, String orderId) {
        this.paidAt = paidAt;
        this.cartId = cartId;
        this.orderId = orderId;
        this.totalAmount = 0;
    }

    public void applyTotalAmount(int amount) {
        if (this.totalAmount != 0) {
            throw new IllegalStateException("이미 금액이 확정된 주문입니다.");
        }
        this.totalAmount = amount;
    }

}

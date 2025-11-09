package org.example.cloudpos.order.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Order{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", length = 26, nullable = false, unique = true)
    private String orderId;

    @Column(name = "cart_id", length = 26, nullable = false, unique = true)
    private String cartId;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

}

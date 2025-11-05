package org.example.cloudpos.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_item_id", length = 26, nullable = false, unique = true)
    private String orderItemId;

    private String productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

}

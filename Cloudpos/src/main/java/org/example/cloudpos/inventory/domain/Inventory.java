package org.example.cloudpos.inventory.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.cloudpos.product.domain.Product;

/**
 * 인벤토리(매장) 엔티티입니다.
 *
 * <p>점주(User)가 보유한 매장을 나타내며,
 * 각 인벤토리는 하나의 상품(Product)을 참조할 수 있습니다.</p>
 *
 * <ul>
 *     <li>product: 본사 상품 참조</li>
 *     <li>inventoryId: ULID 형식의 외부 식별자</li>
 * </ul>
 *
 * @author
 * @since 1.0
 */
@Entity
@Table(name = "inventories")
@Getter
@NoArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 내부 PK

    @Column(name = "inventory_id", nullable = false, unique = true, length = 26)
    private String inventoryId; // ULID

    @Column(nullable = false, length = 100)
    private String name; // 매장명 (예: "스타벅스 강남점")

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 본사 상품 참조

    public Inventory(String inventoryId, String name, Product product) {
        this.inventoryId = inventoryId;
        this.name = name;
        this.product = product;
    }
}

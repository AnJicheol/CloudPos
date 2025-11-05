package org.example.cloudpos.inventory.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.cloudpos.product.domain.Product;

/**
 * 인벤토리(매장) 엔티티.
 *
 * <p>매장 단위로 상품을 관리하며,
 * 한 매장에는 여러 상품이 등록될 수 있고,
 * 한 상품은 하나의 매장에만 속합니다.</p>
 */
@Entity
@Table(name = "inventories",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"inventory_id", "product_id"})
        })
@Getter
@NoArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 매장 외부 식별자 (ULID) */
    @Column(name = "inventory_id", nullable = false, length = 26)
    private String inventoryId;

    /** 매장명 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 본사 상품 참조 (UNIQUE: 동일 상품 중복 등록 불가) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    public Inventory(String inventoryId, String name, Product product) {
        this.inventoryId = inventoryId;
        this.name = name;
        this.product = product;
    }
}

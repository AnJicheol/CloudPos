package org.example.cloudpos.inventory.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.cloudpos.product.domain.Product;

/**
 * {@code Inventory} 엔티티는 매장(Inventory)과 본사 상품(Product) 간의 매핑을 나타냅니다.
 *
 * <p>각 매장은 고유한 외부 식별자(ULID)를 가지며,
 * 해당 매장에 어떤 상품이 등록되어 있는지를 관리합니다.</p>
 *
 * <h2>도메인 규칙</h2>
 * <ul>
 *   <li>하나의 매장에는 여러 상품을 등록할 수 있습니다.</li>
 *   <li>하나의 상품은 오직 하나의 매장에만 속할 수 있습니다.</li>
 *   <li>{@code (inventory_id, product_id)} 조합은 유일해야 합니다.</li>
 * </ul>
 *
 * <h2>매핑 정보</h2>
 * <ul>
 *   <li>테이블명: {@code inventories}</li>
 *   <li>기본키: {@code id}</li>
 *   <li>유니크 제약조건: {@code (inventory_id, product_id)}</li>
 *   <li>{@link Product} 엔티티와 다대일({@code @ManyToOne}) 관계</li>
 * </ul>
 *
 * <h2>용도</h2>
 * <p>매장 생성 후 상품을 등록하거나 제거할 때,
 * 매장-상품 관계를 저장/관리하기 위한 엔티티입니다.
 * 상품 자체의 생성 및 수정은 {@link org.example.cloudpos.product.domain.Product}
 * 도메인에서 관리합니다.</p>
 *
 * @author Esther
 * @since 1.0
 */
@Entity
@Table(
        name = "inventories",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"inventory_id", "product_id"})
        }
)
@Getter
@NoArgsConstructor
public class Inventory {

    /** 내부 식별자 (PK, 자동 증가) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 매장의 외부 식별자 (ULID, 26자 고정) */
    @Column(name = "inventory_id", nullable = false, length = 26)
    private String inventoryId;

    /** 매장명 (예: '강남점', '홍대점') */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 본사 상품 참조.
     *
     * <p>한 상품은 오직 하나의 매장에만 등록될 수 있으며,
     * 동일 상품의 중복 등록을 방지하기 위해 {@code unique = true} 제약이 적용됩니다.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true, unique = true)
    private Product product;

    /**
     * 새 인벤토리 레코드를 생성합니다.
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @param name 매장명
     * @param product 등록할 상품 엔티티
     */
    public Inventory(String inventoryId, String name, Product product) {
        this.inventoryId = inventoryId;
        this.name = name;
        this.product = product;
    }
}

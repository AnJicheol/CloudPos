package inventory.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inventory")
public class Inventory{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 창고 식별자
    @Column(nullable = false)
    private Long inventoryId;

    // 재고 수량
    @Column(nullable = false)
    private int quantity;

    // 상품 ID(Product FK)
    @Column(name = "product_id")
    private Long productId;

    @Builder
    public Inventory(Long inventoryId, int quantity, Long productId) {
        this.inventoryId = inventoryId;
        this.quantity = quantity;
        this.productId = productId;
    }
}

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

    @Column(nullable = false)
    private Long inventoryId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "product_id")
    private Long productId;

    @Builder
    public Inventory(Long inventoryId, int quantity, Long productId) {
        this.inventoryId = inventoryId;
        this.quantity = quantity;
        this.productId = productId;
    }
}

package org.example.cloudpos.inventory.repository;

import org.example.cloudpos.inventory.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // 매장(ULID)로 아무 행이나 하나 (이름을 얻기 위해)
    Optional<Inventory> findFirstByInventoryId(String inventoryId);

    // 해당 매장에 이미 그 상품이 등록되어 있는지
    boolean existsByInventoryIdAndProduct_Id(String inventoryId, Long productId);

    // 그 상품이 전역에서 이미 어떤 매장에 등록되어 있는지 (product_id UNIQUE 보호)
    boolean existsByProduct_Id(Long productId);

    // 매장 존재 여부
    boolean existsByInventoryId(String inventoryId);

    // 목록 조회 시 N+1 방지 (product 즉시 로딩)
    @Query("select i from Inventory i join fetch i.product p where i.inventoryId = :inventoryId")
    List<Inventory> findAllWithProductByInventoryId(String inventoryId);

    // 매장에서 특정 상품 제거 (영향 행 수 반환)
    long deleteByInventoryIdAndProduct_Id(String inventoryId, Long productId);
}

package org.example.cloudpos.inventory.repository;

import org.example.cloudpos.inventory.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 인벤토리 엔티티의 CRUD 작업을 담당하는 JPA 레포지토리입니다.
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * 외부 식별자(ULID)로 인벤토리를 삭제합니다.
     *
     * @param inventoryId 인벤토리 ULID
     */
    void deleteByInventoryId(String inventoryId);
}

package org.example.cloudpos.inventory.repository;

import org.example.cloudpos.inventory.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 인벤토리(매장) 엔티티의 영속성 계층입니다.
 *
 * <p>매장 등록, 삭제, 조회 등의 CRUD 작업을 담당합니다.</p>
 *
 * @author
 * @since 1.0
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * 외부 식별자(ULID)로 인벤토리를 삭제합니다.
     *
     * @param inventoryId 인벤토리 식별자
     */
    void deleteByInventoryId(String inventoryId);
}

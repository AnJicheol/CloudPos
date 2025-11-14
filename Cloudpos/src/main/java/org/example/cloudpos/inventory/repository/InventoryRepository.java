package org.example.cloudpos.inventory.repository;

import org.example.cloudpos.inventory.domain.Inventory;
import org.example.cloudpos.inventory.service.InventoryServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * 인벤토리(매장) 엔티티에 대한 데이터 접근을 담당하는 Spring Data JPA 리포지토리.
 *
 * <p>매장과 상품 간의 매핑 데이터를 조회·검증·삭제하기 위한
 * 파생 쿼리 메서드 및 JPQL 기반 커스텀 쿼리를 제공합니다.</p>
 *
 * <h2>주요 기능</h2>
 * <ul>
 *   <li>매장(ULID) 기준 조회 및 존재 여부 확인</li>
 *   <li>매장 내 상품 중복 등록 방지 검증</li>
 *   <li>상품이 다른 매장에 이미 등록되어 있는지 확인</li>
 *   <li>상품 즉시 로딩(fetch join)을 통한 N+1 문제 방지</li>
 *   <li>매장 내 상품 제거</li>
 * </ul>
 *
 * @see org.example.cloudpos.inventory.domain.Inventory
 * @see InventoryServiceImpl
 * @since 1.0
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * 주어진 {@code inventoryId}에 해당하는 매장의 행 중 하나를 조회합니다.
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @return 매장 레코드 중 하나, 없으면 {@code null}
     */
    Inventory findFirstByInventoryId(String inventoryId);


    /**
     * 주어진 매장 ULID가 존재하는지 여부를 확인합니다.
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @return 매장이 존재하면 {@code true}
     */
    boolean existsByInventoryId(String inventoryId);

    /**
     * 지정한 매장에서 ACTIVE 상태인 상품들만 조회합니다.
     *
     * <p>상품 엔티티를 fetch join하여 N+1 문제를 방지합니다.</p>
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @return ACTIVE 상태의 상품이 포함된 인벤토리 목록
     */
    @Query("""
        select i
        from Inventory i
        join fetch i.product p
        where i.inventoryId = :inventoryId
          and i.status = org.example.cloudpos.inventory.domain.InventoryProductStatus.ACTIVE
    """)
    List<Inventory> findActiveProductsByInventoryId(String inventoryId);

    /**
     * 지정한 매장에서 지정한 상품 매핑을 조회합니다.
     *
     * @param inventoryId 매장 외부 식별자 (ULID)
     * @param productId   상품 식별자
     * @return 매핑된 {@link Inventory} 엔티티, 없으면 {@code null}
     */
    Inventory findByInventoryIdAndProduct_ProductId(String inventoryId, String productId);
}

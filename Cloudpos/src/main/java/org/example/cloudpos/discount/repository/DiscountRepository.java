package org.example.cloudpos.discount.repository;

import org.example.cloudpos.discount.domain.Discount;
import org.example.cloudpos.discount.dto.kiosk.DiscountKioskResponse;
import org.example.cloudpos.discount.dto.owner.DiscountOwnerResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String> {

    @Query("""
        select d
        from Discount d
        where d.inventoryId = :inventoryId
        and d.discountStart <= :now
        and d.discountEnd >= :now
    """)
    List<DiscountKioskResponse> findInventoryCustomerDiscount
            (@Param("inventoryId") String inventoryId,
             @Param("now") LocalDateTime now);

    @Query("""
        select d
        from Discount d
        where d.inventoryId = :inventoryId
    """)
    List<DiscountOwnerResponse> findInventoryOwnerDiscount(@Param("inventoryId") String inventoryId);

    // 날짜검증

    @Query("""
        select d
        from Discount d
        where d.productId = :productId
            and d.inventoryId = :inventoryId
            and d.discountId = :customerDiscountId
            and d.discountStart <= :now
            and d.discountEnd >= :now
    """)
    Optional<Discount> selectKioskDiscount
            (@Param("productId") String productId,
             @Param("inventoryId") String inventoryId,
             @Param("discountId") String customerDiscountId,
             @Param("now")  LocalDateTime now
            );


    @Query("""
        select d
        from Discount d
        where d.discountId = :discountId
    """)
    Optional<Discount> findByDiscountId(@Param("discountId") String discountId);

    @Query("""
        select d
        from Discount d
        where d.discountId = :discountId
            and d.inventoryId = :inventoryId
            and d.productId = :productId
    """)
    Optional<Discount> disocuntSearch
            (@Param("productId") String productId,
             @Param("discountId") String discountId,
             @Param("inventoryId") String inventoryId
            );

}



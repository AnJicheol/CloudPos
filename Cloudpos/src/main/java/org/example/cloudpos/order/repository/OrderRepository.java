package org.example.cloudpos.order.repository;

import org.example.cloudpos.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findOrderByOrderId(@Param("orderId") String orderId);

    @Query("select o.cartId from Order o where o.orderId = :orderId")
    String findCartIdByOrderId(@Param("orderId") String orderId);
}

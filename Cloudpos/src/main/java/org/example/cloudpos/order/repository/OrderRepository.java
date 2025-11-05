package org.example.cloudpos.order.repository;

import org.example.cloudpos.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}

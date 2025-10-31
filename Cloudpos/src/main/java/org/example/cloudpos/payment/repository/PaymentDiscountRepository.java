package org.example.cloudpos.payment.repository;

import org.example.cloudpos.payment.domain.PaymentDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PaymentDiscountRepository extends JpaRepository<PaymentDiscount, Long> {
}

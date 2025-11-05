package org.example.cloudpos.payment.repository;

import org.example.cloudpos.payment.domain.TossPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TossPaymentRepository extends JpaRepository<TossPayment, Long> {
}

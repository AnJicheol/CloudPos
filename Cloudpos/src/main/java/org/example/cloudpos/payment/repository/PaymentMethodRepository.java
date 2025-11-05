package org.example.cloudpos.payment.repository;

import org.example.cloudpos.payment.domain.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
}

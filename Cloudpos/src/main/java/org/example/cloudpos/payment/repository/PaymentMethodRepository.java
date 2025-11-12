package org.example.cloudpos.payment.repository;

import org.example.cloudpos.payment.domain.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    // 코드로 조회 (중복 등록 방지용)
    Optional<PaymentMethod> findByCode(String code);

    // 활성화된 결제수단만 조회
    List<PaymentMethod> findByIsActiveTrue();

}

package org.example.cloudpos.payment.repository;

import org.example.cloudpos.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * PaymentRepository
 *
 * 결제(Payment) 엔티티에 대한 데이터 접근 레이어.
 * Spring Data JPA가 기본 CRUD 기능을 자동으로 제공함.
 *
 * 주요 기능:
 *  - 결제 생성, 조회, 수정, 삭제
 *  - 결제 ID 기준 조회
 *  - 추후 커스텀 쿼리(@Query)로 상태별 조회 등 확장 가능
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);

}

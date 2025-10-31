package org.example.cloudpos.payment.repository;

import org.example.cloudpos.payment.domain.PaymentDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * PaymentDiscountRepository
 *
 * 결제별 할인 내역(PaymentDiscount) 엔티티에 대한 Repository.
 * 결제와 할인 정책의 관계 데이터를 관리함.
 *
 * 주요 기능:
 *  - 결제에 적용된 할인 내역 저장 및 조회
 *  - 할인 정책별 통계 또는 내역 조회 시 활용 가능
 */
@Repository
public interface PaymentDiscountRepository extends JpaRepository<PaymentDiscount, Long> {
}

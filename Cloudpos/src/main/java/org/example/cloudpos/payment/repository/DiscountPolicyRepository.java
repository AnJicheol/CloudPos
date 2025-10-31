package org.example.cloudpos.payment.repository;

import org.example.cloudpos.payment.domain.DiscountPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * DiscountPolicyRepository
 *
 * 할인 정책(DiscountPolicy) 엔티티에 대한 Repository.
 * 활성화 여부, 유효기간, 할인 타입 등으로 검색 확장이 가능함.
 *
 * 주요 기능:
 *  - 할인 정책 CRUD
 *  - 정책 활성화 여부(isActive) 기반 필터링 (추후 커스텀 메서드 추가 가능)
 */
@Repository
public interface DiscountPolicyRepository extends JpaRepository<DiscountPolicy, Long> {
}

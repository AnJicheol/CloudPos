package org.example.cloudpos.payment.repository;

import org.example.cloudpos.payment.domain.DiscountPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountPolicyRepository extends JpaRepository<DiscountPolicy, Long> {
}

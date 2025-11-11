package org.example.cloudpos.auth.repository;


import org.example.cloudpos.auth.AuthProvider;
import org.example.cloudpos.auth.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserIdAndProvider(Long userId, AuthProvider provider);
}


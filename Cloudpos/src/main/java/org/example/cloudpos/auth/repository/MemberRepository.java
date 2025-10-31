package org.example.cloudpos.auth.repository;


import org.example.cloudpos.auth.domain.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
}


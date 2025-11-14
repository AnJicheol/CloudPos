package org.example.cloudpos.auth.repository;

import org.example.cloudpos.auth.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}

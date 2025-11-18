package org.example.cloudpos.auth.repository;

import org.example.cloudpos.auth.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByProviderAndProviderUserId(String provider, String providerUserId);
}

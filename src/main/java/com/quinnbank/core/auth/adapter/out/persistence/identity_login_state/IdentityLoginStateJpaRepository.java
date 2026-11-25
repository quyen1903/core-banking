package com.quinnbank.core.auth.adapter.out.persistence.identity_login_state;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;

public interface IdentityLoginStateJpaRepository extends JpaRepository<IdentityLoginStateJpaEntity, UUID> {
    Optional<IdentityLoginStateJpaEntity> findByUserId(UUID id);
}

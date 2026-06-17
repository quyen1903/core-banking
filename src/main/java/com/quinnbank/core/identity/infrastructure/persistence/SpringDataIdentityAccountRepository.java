package com.quinnbank.core.identity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataIdentityAccountRepository extends JpaRepository<IdentityAccountJpaEntity, UUID> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

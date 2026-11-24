package com.quinnbank.core.auth.adapter.out.persistence.identity_credential;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityCredentialJpaRepository extends JpaRepository<IdentityCredentialJpaEntity, UUID> {
    // IdentityCredentialJpaEntity saveAndFlush(IdentityCredentialJpaEntity entity);
    @EntityGraph(attributePaths = "user")
    Optional<IdentityCredentialJpaEntity> findByUserId(UUID userId);
}

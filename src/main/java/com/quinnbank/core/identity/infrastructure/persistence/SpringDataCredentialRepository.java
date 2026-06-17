package com.quinnbank.core.identity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataCredentialRepository extends JpaRepository<CredentialJpaEntity, UUID> {

    Optional<CredentialJpaEntity> findByIdentityAccountIdAndActiveTrue(UUID identityAccountId);
}

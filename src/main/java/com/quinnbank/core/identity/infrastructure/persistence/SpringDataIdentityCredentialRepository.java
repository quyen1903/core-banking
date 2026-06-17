package com.quinnbank.core.identity.infrastructure.persistence;

import com.quinnbank.core.identity.domain.model.IdentityCredentialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface SpringDataIdentityCredentialRepository extends JpaRepository<IdentityCredentialJpaEntity, Long> {

    Optional<IdentityCredentialJpaEntity> findByIdentityUserIdAndType(
            Long identityUserId,
            IdentityCredentialType type
    );
}

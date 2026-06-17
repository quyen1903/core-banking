package com.quinnbank.core.identity.infrastructure.persistence;

import com.quinnbank.core.identity.domain.model.PermissionCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface SpringDataIdentityPermissionRepository extends JpaRepository<IdentityPermissionJpaEntity, Long> {

    Optional<IdentityPermissionJpaEntity> findByCode(PermissionCode code);
}

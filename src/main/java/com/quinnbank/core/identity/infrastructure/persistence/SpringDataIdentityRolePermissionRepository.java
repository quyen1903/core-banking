package com.quinnbank.core.identity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataIdentityRolePermissionRepository
        extends JpaRepository<IdentityRolePermissionJpaEntity, IdentityRolePermissionJpaId> {
}

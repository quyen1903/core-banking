package com.quinnbank.core.identity.infrastructure.persistence;

import com.quinnbank.core.identity.domain.model.RoleCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface SpringDataIdentityRoleRepository extends JpaRepository<IdentityRoleJpaEntity, Long> {

    Optional<IdentityRoleJpaEntity> findByCode(RoleCode code);
}

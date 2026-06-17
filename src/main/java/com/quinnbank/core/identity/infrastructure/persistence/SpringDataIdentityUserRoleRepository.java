package com.quinnbank.core.identity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface SpringDataIdentityUserRoleRepository extends JpaRepository<IdentityUserRoleJpaEntity, IdentityUserRoleJpaId> {

    boolean existsByIdentityUserIdAndRoleId(Long identityUserId, Long roleId);

    List<IdentityUserRoleJpaEntity> findByIdentityUserId(Long identityUserId);

    void deleteByIdentityUserIdAndRoleId(Long identityUserId, Long roleId);
}

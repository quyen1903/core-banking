package com.quinnbank.core.identity.infrastructure.persistence;

import com.quinnbank.core.identity.application.port.out.RoleRepositoryPort;
import com.quinnbank.core.identity.application.port.out.UserRoleRepositoryPort;
import com.quinnbank.core.identity.domain.model.Role;
import com.quinnbank.core.identity.domain.model.RoleCode;
import com.quinnbank.core.identity.domain.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
class IdentityRolePersistenceAdapter implements RoleRepositoryPort, UserRoleRepositoryPort {

    private final SpringDataIdentityRoleRepository roleRepository;
    private final SpringDataIdentityUserRoleRepository userRoleRepository;
    private final IdentityPersistenceMapper mapper = new IdentityPersistenceMapper();

    @Override
    public java.util.Optional<Role> findByCode(RoleCode code) {
        return roleRepository.findByCode(code)
                .map(mapper::toDomain);
    }

    @Override
    public UserRole save(UserRole userRole) {
        userRoleRepository.save(mapper.toIdentityUserRoleEntity(userRole));
        return userRole;
    }

    @Override
    public boolean existsByIdentityUserIdAndRoleId(Long identityUserId, Long roleId) {
        return userRoleRepository.existsByIdentityUserIdAndRoleId(identityUserId, roleId);
    }

    @Override
    public void remove(Long identityUserId, Long roleId) {
        userRoleRepository.deleteByIdentityUserIdAndRoleId(identityUserId, roleId);
    }

    @Override
    public Set<RoleCode> findRoleCodesByIdentityUserId(Long identityUserId) {
        if (identityUserId == null) {
            return Set.of();
        }

        Set<Long> roleIds = userRoleRepository.findByIdentityUserId(identityUserId).stream()
                .map(IdentityUserRoleJpaEntity::getRoleId)
                .collect(Collectors.toUnmodifiableSet());
        if (roleIds.isEmpty()) {
            return Set.of();
        }

        return Collections.unmodifiableSet(roleRepository.findAllById(roleIds).stream()
                .map(IdentityRoleJpaEntity::getCode)
                .collect(Collectors.toSet()));
    }
}

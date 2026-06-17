package com.quinnbank.core.identity.application.port.out;

import com.quinnbank.core.identity.domain.model.RoleCode;
import com.quinnbank.core.identity.domain.model.UserRole;

import java.util.Set;

public interface UserRoleRepositoryPort {

    UserRole save(UserRole userRole);

    boolean existsByIdentityUserIdAndRoleId(Long identityUserId, Long roleId);

    void remove(Long identityUserId, Long roleId);

    Set<RoleCode> findRoleCodesByIdentityUserId(Long identityUserId);
}

package com.quinnbank.core.identity.application.port.out;

import com.quinnbank.core.identity.domain.model.Role;
import com.quinnbank.core.identity.domain.model.RoleCode;

import java.util.Optional;

public interface RoleRepositoryPort {

    Optional<Role> findByCode(RoleCode code);
}

package com.quinnbank.core.identity.application;

import com.quinnbank.core.identity.domain.model.RoleCode;

public class IdentityRoleNotFoundException extends RuntimeException {

    private IdentityRoleNotFoundException(String message) {
        super(message);
    }

    public static IdentityRoleNotFoundException byCode(RoleCode code) {
        return new IdentityRoleNotFoundException("identity role was not found: " + code);
    }
}

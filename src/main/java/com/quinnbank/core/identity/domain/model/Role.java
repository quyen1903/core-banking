package com.quinnbank.core.identity.domain.model;

public final class Role {

    private final Long id;
    private final RoleCode code;

    private Role(Long id, RoleCode code) {
        this.id = id;
        this.code = code;
    }

    public static Role restore(Long id, RoleCode code) {
        if (id == null) {
            throw new IllegalArgumentException("role id is required");
        }
        if (code == null) {
            throw new IllegalArgumentException("role code is required");
        }

        return new Role(id, code);
    }

    public Long id() {
        return id;
    }

    public RoleCode code() {
        return code;
    }
}

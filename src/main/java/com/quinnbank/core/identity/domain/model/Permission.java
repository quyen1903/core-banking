package com.quinnbank.core.identity.domain.model;

public final class Permission {

    private final Long id;
    private final PermissionCode code;

    private Permission(Long id, PermissionCode code) {
        this.id = id;
        this.code = code;
    }

    public static Permission restore(Long id, PermissionCode code) {
        if (id == null) {
            throw new IllegalArgumentException("permission id is required");
        }
        if (code == null) {
            throw new IllegalArgumentException("permission code is required");
        }

        return new Permission(id, code);
    }

    public Long id() {
        return id;
    }

    public PermissionCode code() {
        return code;
    }
}

package com.quinnbank.core.auth.adapter.out.persistence.identity_user;

import com.quinnbank.core.auth.domain.model.IdentityUser;

// Converts the domain IdentityUser into a new JPA entity for persistence.
public class IdentityUserPersistenceMapper {

    private IdentityUserPersistenceMapper() {}

    public static IdentityUserJpaEntity toNewEntity(IdentityUser identityUser) {
        IdentityUserJpaEntity entity = new IdentityUserJpaEntity();
        entity.id = identityUser.getId();
        entity.publicId = identityUser.getId();
        entity.username = identityUser.getUsername();
        entity.usernameNormalized = IdentityUser.normalizeUsername(identityUser.getUsername());
        entity.ownerId = identityUser.getOwnerId();
        entity.ownerType = identityUser.getOwnerType();
        entity.status = identityUser.getStatus();
        entity.securityVersion = identityUser.getSecurityVersion();
        entity.createdAt = identityUser.getCreateAt();
        entity.updatedAt = identityUser.getUpdateAt();
        entity.version = identityUser.getVersion();
        return entity;
    }
}

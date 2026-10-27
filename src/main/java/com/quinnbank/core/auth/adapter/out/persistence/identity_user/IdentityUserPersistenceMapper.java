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
        entity.ownerType = identityUser.getOwnerType();
        return entity;
    }
}

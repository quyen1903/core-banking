package com.quinnbank.core.auth.adapter.out.persistence.identity_user;

import com.quinnbank.core.auth.domain.model.Auth;

//convert the domain IdentityUSer into new JPA entity for persisting into database
public class IdentityUserPersistenceMapper {

    private IdentityUserPersistenceMapper() {}

    static IdentityUserJpaEntity toNewEntity(Auth auth) {
        IdentityUserJpaEntity entity = new IdentityUserJpaEntity();
        entity.id = auth.getId();
        entity.publicId = auth.getId();
        entity.username = auth.getUsername();
        entity.usernameNormalized = Auth.normalizeUsername(auth.getUsername());
        entity.ownerType = auth.getOwnerType();
        return entity;
    }
}

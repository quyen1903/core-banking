package com.quinnbank.core.auth.adapter.out.persistence.identity_credential;

import com.quinnbank.core.auth.domain.model.IdentityCredential;
 
public class IdentityCredentialPersistenceMapper {
    private IdentityCredentialPersistenceMapper() {
        // Private constructor to prevent instantiation
    }

    public static IdentityCredentialJpaEntity toNewEntity(IdentityCredential identityCredential) {
        IdentityCredentialJpaEntity entity = new IdentityCredentialJpaEntity();
        entity.userId = identityCredential.getIdentityPublicId();
        entity.passwordHash = identityCredential.getPasswordHash();
        entity.passwordChangedAt = identityCredential.getPasswordChangedAt();
        entity.mustChangePassword = identityCredential.isMustChangePassword();
        return entity;
    }

}

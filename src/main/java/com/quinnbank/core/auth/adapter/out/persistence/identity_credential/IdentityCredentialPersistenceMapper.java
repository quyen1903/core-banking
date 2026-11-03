package com.quinnbank.core.auth.adapter.out.persistence.identity_credential;

import com.quinnbank.core.auth.adapter.out.persistence.identity_user.IdentityUserJpaEntity;
import com.quinnbank.core.auth.domain.model.IdentityCredential;
 
public class IdentityCredentialPersistenceMapper {
    
    private IdentityCredentialPersistenceMapper() {
        // Private constructor to prevent instantiation
    }

    public static IdentityCredentialJpaEntity toNewEntity(
        IdentityCredential identityCredential,
        IdentityUserJpaEntity userRef
    ) {
        IdentityCredentialJpaEntity entity = new IdentityCredentialJpaEntity();
        
        entity.userId = identityCredential.getUserId();
        entity.user = userRef;
        entity.passwordHash = identityCredential.getPasswordHash();
        entity.passwordChangedAt = identityCredential.getPasswordChangedAt();
        entity.mustChangePassword = identityCredential.isMustChangePassword();
        entity.createdAt = identityCredential.getCreatedAt();
        entity.updatedAt = identityCredential.getUpdatedAt();
        return entity;
    }

}

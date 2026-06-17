package com.quinnbank.core.identity.infrastructure.persistence;

import com.quinnbank.core.identity.domain.model.Credential;
import com.quinnbank.core.identity.domain.model.IdentityAccount;

class IdentityAccountPersistenceMapper {

    IdentityAccountJpaEntity toIdentityAccountEntity(IdentityAccount account) {
        return IdentityAccountJpaEntity.create(
                account.id(),
                account.username(),
                account.email(),
                account.subjectType(),
                account.subjectId(),
                account.status(),
                account.createdAt(),
                account.updatedAt(),
                account.version()
        );
    }

    CredentialJpaEntity toCredentialEntity(IdentityAccount account) {
        Credential credential = account.credential();
        return CredentialJpaEntity.create(
                credential.id(),
                credential.identityAccountId(),
                credential.type(),
                credential.passwordHash(),
                credential.active(),
                credential.createdAt(),
                credential.updatedAt(),
                credential.version()
        );
    }

    IdentityAccount toDomain(IdentityAccountJpaEntity account, CredentialJpaEntity credential) {
        return IdentityAccount.restore(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.getSubjectType(),
                account.getSubjectId(),
                account.getStatus(),
                Credential.restore(
                        credential.getId(),
                        credential.getIdentityAccountId(),
                        credential.getCredentialType(),
                        credential.getPasswordHash(),
                        credential.isActive(),
                        credential.getCreatedAt(),
                        credential.getUpdatedAt(),
                        credential.getVersion()
                ),
                account.getCreatedAt(),
                account.getUpdatedAt(),
                account.getVersion()
        );
    }
}

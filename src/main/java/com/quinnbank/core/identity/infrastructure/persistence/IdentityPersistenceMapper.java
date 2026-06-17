package com.quinnbank.core.identity.infrastructure.persistence;

import com.quinnbank.core.identity.domain.model.IdentityCredential;
import com.quinnbank.core.identity.domain.model.IdentityUser;
import com.quinnbank.core.identity.domain.model.Role;
import com.quinnbank.core.identity.domain.model.UserRole;

class IdentityPersistenceMapper {

    IdentityUserJpaEntity toIdentityUserEntity(IdentityUser user) {
        return IdentityUserJpaEntity.create(
                user.id(),
                user.publicId(),
                user.ownerType(),
                user.ownerId(),
                user.username(),
                user.email(),
                user.phoneNumber(),
                user.status(),
                user.createdAt(),
                user.updatedAt(),
                user.version()
        );
    }

    IdentityUser toDomain(IdentityUserJpaEntity user) {
        return IdentityUser.restore(
                user.getId(),
                user.getPublicId(),
                user.getOwnerType(),
                user.getOwnerId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getVersion()
        );
    }

    IdentityCredentialJpaEntity toIdentityCredentialEntity(IdentityCredential credential) {
        return IdentityCredentialJpaEntity.create(
                credential.id(),
                credential.identityUserId(),
                credential.type(),
                credential.passwordHash(),
                credential.passwordChangedOnUtc(),
                credential.mustChangePassword(),
                credential.createdOnUtc()
        );
    }

    IdentityCredential toDomain(IdentityCredentialJpaEntity credential) {
        return IdentityCredential.restore(
                credential.getId(),
                credential.getIdentityUserId(),
                credential.getType(),
                credential.getPasswordHash(),
                credential.getPasswordChangedOnUtc(),
                credential.isMustChangePassword(),
                credential.getCreatedOnUtc()
        );
    }

    Role toDomain(IdentityRoleJpaEntity role) {
        return Role.restore(role.getId(), role.getCode());
    }

    IdentityUserRoleJpaEntity toIdentityUserRoleEntity(UserRole userRole) {
        return IdentityUserRoleJpaEntity.create(
                userRole.identityUserId(),
                userRole.roleId(),
                userRole.assignedAt()
        );
    }
}

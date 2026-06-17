package com.quinnbank.core.identity.infrastructure.persistence;

import com.quinnbank.core.identity.domain.model.IdentityOwnerType;
import com.quinnbank.core.identity.domain.model.IdentityUserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "identity_user")
class IdentityUserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 50)
    private IdentityOwnerType ownerType;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(nullable = false, unique = true, length = 120)
    private String username;

    @Column(unique = true, length = 255)
    private String email;

    @Column(name = "phone_number", unique = true, length = 50)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private IdentityUserStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    static IdentityUserJpaEntity create(
            Long id,
            UUID publicId,
            IdentityOwnerType ownerType,
            Long ownerId,
            String username,
            String email,
            String phoneNumber,
            IdentityUserStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long version
    ) {
        IdentityUserJpaEntity entity = new IdentityUserJpaEntity();
        entity.id = id;
        entity.publicId = publicId;
        entity.ownerType = ownerType;
        entity.ownerId = ownerId;
        entity.username = username;
        entity.email = email;
        entity.phoneNumber = phoneNumber;
        entity.status = status;
        entity.createdAt = createdAt;
        entity.updatedAt = updatedAt;
        entity.version = version;
        return entity;
    }
}

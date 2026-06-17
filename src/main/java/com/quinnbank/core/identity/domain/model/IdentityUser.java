package com.quinnbank.core.identity.domain.model;

import com.quinnbank.core.identity.domain.event.IdentityUserCreatedEvent;
import com.quinnbank.core.identity.domain.exception.IdentityUserCreationRejectedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class IdentityUser {

    private final Long id;
    private final UUID publicId;
    private final IdentityOwnerType ownerType;
    private final Long ownerId;
    private final String username;
    private final String email;
    private final String phoneNumber;
    private final IdentityUserStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final long version;
    private final List<Object> domainEvents = new ArrayList<>();

    private IdentityUser(
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
        this.id = id;
        this.publicId = publicId;
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static IdentityUser create(
            IdentityOwnerType ownerType,
            Long ownerId,
            String username,
            String email,
            String phoneNumber,
            LocalDateTime createdAt
    ) {
        requireOwner(ownerType, ownerId);
        requireUsername(username);
        if (createdAt == null) {
            throw new IdentityUserCreationRejectedException("identity user creation time is required");
        }

        IdentityUser user = new IdentityUser(
                null,
                UUID.randomUUID(),
                ownerType,
                ownerId,
                normalizeUsername(username),
                normalizeEmail(email),
                normalizePhoneNumber(phoneNumber),
                IdentityUserStatus.PENDING_ACTIVATION,
                createdAt,
                createdAt,
                0
        );
        user.domainEvents.add(new IdentityUserCreatedEvent(
                user.publicId,
                user.ownerType,
                user.ownerId,
                createdAt
        ));

        return user;
    }

    public static IdentityUser restore(
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
        if (id == null) {
            throw new IllegalArgumentException("identity user id is required");
        }
        if (publicId == null) {
            throw new IllegalArgumentException("identity user public id is required");
        }
        requireOwner(ownerType, ownerId);
        requireUsername(username);
        if (status == null) {
            throw new IllegalArgumentException("identity user status is required");
        }
        if (createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("identity user timestamps are required");
        }

        return new IdentityUser(
                id,
                publicId,
                ownerType,
                ownerId,
                normalizeUsername(username),
                normalizeEmail(email),
                normalizePhoneNumber(phoneNumber),
                status,
                createdAt,
                updatedAt,
                version
        );
    }

    public IdentityUser changeStatus(IdentityUserStatus newStatus, LocalDateTime changedAt) {
        if (newStatus == null) {
            throw new IdentityUserCreationRejectedException("identity user status is required");
        }
        if (changedAt == null) {
            throw new IdentityUserCreationRejectedException("identity user status change time is required");
        }

        return new IdentityUser(
                id,
                publicId,
                ownerType,
                ownerId,
                username,
                email,
                phoneNumber,
                newStatus,
                createdAt,
                changedAt,
                version
        );
    }

    public List<Object> pullDomainEvents() {
        List<Object> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public Long id() {
        return id;
    }

    public UUID publicId() {
        return publicId;
    }

    public IdentityOwnerType ownerType() {
        return ownerType;
    }

    public Long ownerId() {
        return ownerId;
    }

    public String username() {
        return username;
    }

    public String email() {
        return email;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public IdentityUserStatus status() {
        return status;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }

    public long version() {
        return version;
    }

    public static String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        return username.trim().toLowerCase();
    }

    public static String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return email.trim().toLowerCase();
    }

    public static String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return null;
        }

        return phoneNumber.trim();
    }

    private static void requireUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IdentityUserCreationRejectedException("identity username is required");
        }
    }

    private static void requireOwner(IdentityOwnerType ownerType, Long ownerId) {
        if (ownerType == null) {
            throw new IdentityUserCreationRejectedException("identity owner type is required");
        }
        if (ownerId == null || ownerId <= 0) {
            throw new IdentityUserCreationRejectedException("identity owner id is required");
        }
    }
}

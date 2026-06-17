package com.quinnbank.core.identity.domain.model;

import com.quinnbank.core.identity.domain.event.IdentityAccountCreatedEvent;
import com.quinnbank.core.identity.domain.exception.IdentityAccountCreationRejectedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class IdentityAccount {

    private final UUID id;
    private final String username;
    private final String email;
    private final IdentitySubjectType subjectType;
    private final UUID subjectId;
    private final IdentityAccountStatus status;
    private final Credential credential;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final long version;
    private final List<Object> domainEvents = new ArrayList<>();

    private IdentityAccount(
            UUID id,
            String username,
            String email,
            IdentitySubjectType subjectType,
            UUID subjectId,
            IdentityAccountStatus status,
            Credential credential,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long version
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.subjectType = subjectType;
        this.subjectId = subjectId;
        this.status = status;
        this.credential = credential;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static IdentityAccount create(
            String username,
            String email,
            IdentitySubjectType subjectType,
            UUID subjectId,
            String passwordHash,
            LocalDateTime createdAt
    ) {
        requireUsername(username);
        requireSubject(subjectType, subjectId);
        if (createdAt == null) {
            throw new IdentityAccountCreationRejectedException("identity account creation time is required");
        }

        UUID identityAccountId = UUID.randomUUID();
        IdentityAccount account = new IdentityAccount(
                identityAccountId,
                normalizeUsername(username),
                normalizeEmail(email),
                subjectType,
                subjectId,
                IdentityAccountStatus.ACTIVE,
                Credential.password(identityAccountId, passwordHash, createdAt),
                createdAt,
                createdAt,
                0
        );
        account.domainEvents.add(new IdentityAccountCreatedEvent(
                account.id,
                account.subjectType,
                account.subjectId,
                createdAt
        ));

        return account;
    }

    public static IdentityAccount restore(
            UUID id,
            String username,
            String email,
            IdentitySubjectType subjectType,
            UUID subjectId,
            IdentityAccountStatus status,
            Credential credential,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long version
    ) {
        if (id == null) {
            throw new IllegalArgumentException("identity account id is required");
        }
        requireUsername(username);
        requireSubject(subjectType, subjectId);
        if (status == null) {
            throw new IllegalArgumentException("identity account status is required");
        }
        if (credential == null) {
            throw new IllegalArgumentException("credential is required");
        }
        if (createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("identity account timestamps are required");
        }

        return new IdentityAccount(
                id,
                normalizeUsername(username),
                normalizeEmail(email),
                subjectType,
                subjectId,
                status,
                credential,
                createdAt,
                updatedAt,
                version
        );
    }

    public List<Object> pullDomainEvents() {
        List<Object> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public UUID id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String email() {
        return email;
    }

    public IdentitySubjectType subjectType() {
        return subjectType;
    }

    public UUID subjectId() {
        return subjectId;
    }

    public IdentityAccountStatus status() {
        return status;
    }

    public Credential credential() {
        return credential;
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

    private static void requireUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IdentityAccountCreationRejectedException("identity username is required");
        }
    }

    private static void requireSubject(IdentitySubjectType subjectType, UUID subjectId) {
        if (subjectType == null) {
            throw new IdentityAccountCreationRejectedException("identity subject type is required");
        }
        if ((subjectType == IdentitySubjectType.EMPLOYEE || subjectType == IdentitySubjectType.CUSTOMER)
                && subjectId == null) {
            throw new IdentityAccountCreationRejectedException("identity subject id is required");
        }
        if ((subjectType == IdentitySubjectType.SERVICE_ACCOUNT || subjectType == IdentitySubjectType.SYSTEM)
                && subjectId != null) {
            throw new IdentityAccountCreationRejectedException("service and system identities must not reference employee or customer subjects");
        }
    }
}

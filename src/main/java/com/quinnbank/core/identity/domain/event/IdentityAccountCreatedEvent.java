package com.quinnbank.core.identity.domain.event;

import com.quinnbank.core.identity.domain.model.IdentitySubjectType;

import java.time.LocalDateTime;
import java.util.UUID;

public record IdentityAccountCreatedEvent(
        UUID identityAccountId,
        IdentitySubjectType subjectType,
        UUID subjectId,
        LocalDateTime occurredAt
) {
}

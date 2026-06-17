package com.quinnbank.core.identity.application.command;

import com.quinnbank.core.identity.domain.model.IdentitySubjectType;

import java.util.UUID;

public record CreateIdentityAccountCommand(
        String username,
        String email,
        IdentitySubjectType subjectType,
        UUID subjectId,
        String initialPassword
) {
}

package com.quinnbank.core.identity.application.result;

import com.quinnbank.core.identity.domain.model.IdentityAccount;
import com.quinnbank.core.identity.domain.model.IdentityAccountStatus;
import com.quinnbank.core.identity.domain.model.IdentitySubjectType;

import java.util.UUID;

public record IdentityAccountSnapshot(
        UUID id,
        String username,
        String email,
        IdentitySubjectType subjectType,
        UUID subjectId,
        IdentityAccountStatus status
) {

    public static IdentityAccountSnapshot from(IdentityAccount account) {
        return new IdentityAccountSnapshot(
                account.id(),
                account.username(),
                account.email(),
                account.subjectType(),
                account.subjectId(),
                account.status()
        );
    }
}

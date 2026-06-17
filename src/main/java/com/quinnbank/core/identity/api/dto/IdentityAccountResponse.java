package com.quinnbank.core.identity.api.dto;

import com.quinnbank.core.identity.application.result.IdentityAccountSnapshot;

import java.util.UUID;

public record IdentityAccountResponse(
        UUID id,
        String username,
        String email,
        String subjectType,
        UUID subjectId,
        String status
) {

    public static IdentityAccountResponse from(IdentityAccountSnapshot identityAccount) {
        return new IdentityAccountResponse(
                identityAccount.id(),
                identityAccount.username(),
                identityAccount.email(),
                identityAccount.subjectType().name(),
                identityAccount.subjectId(),
                identityAccount.status().name()
        );
    }
}

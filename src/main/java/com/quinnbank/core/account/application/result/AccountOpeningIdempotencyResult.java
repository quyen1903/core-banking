package com.quinnbank.core.account.application.result;

import java.util.UUID;

public record AccountOpeningIdempotencyResult(
        String idempotencyKey,
        String requestFingerprint,
        UUID accountId
) {

    public boolean matches(String candidateFingerprint) {
        return requestFingerprint.equals(candidateFingerprint);
    }
}

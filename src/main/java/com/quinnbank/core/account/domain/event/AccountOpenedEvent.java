package com.quinnbank.core.account.domain.event;

import com.quinnbank.core.account.domain.model.AccountNumber;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccountOpenedEvent(
        UUID accountId,
        AccountNumber accountNumber,
        UUID customerId,
        UUID productId,
        String idempotencyKey,
        LocalDateTime occurredAt
) {
}

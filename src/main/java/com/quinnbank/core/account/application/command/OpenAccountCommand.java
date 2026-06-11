package com.quinnbank.core.account.application.command;

import java.util.UUID;

public record OpenAccountCommand(
        UUID customerId,
        String productCode,
        String idempotencyKey
) {
}

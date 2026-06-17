package com.quinnbank.core.ledger.application.result;

import com.quinnbank.core.ledger.domain.model.LedgerEntry;

import java.math.BigDecimal;
import java.util.UUID;

public record LedgerEntrySnapshot(
        UUID id,
        int lineNumber,
        UUID accountId,
        String side,
        String currency,
        BigDecimal amount
) {

    public static LedgerEntrySnapshot from(LedgerEntry entry) {
        return new LedgerEntrySnapshot(
                entry.id(),
                entry.lineNumber(),
                entry.accountId(),
                entry.side().name(),
                entry.amount().currency().getCurrencyCode(),
                entry.amount().amount()
        );
    }
}

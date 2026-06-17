package com.quinnbank.core.ledger.application.command;

import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

public record PostLedgerJournalCommand(
        String sourceCommandId,
        String idempotencyKey,
        String actorType,
        String actorId,
        String businessReason,
        LocalDate postingDate,
        LocalDate valueDate,
        Currency currency,
        String correlationId,
        List<PostLedgerEntryCommand> entries
) {
}

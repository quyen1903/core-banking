package com.quinnbank.core.ledger.application.result;

import com.quinnbank.core.ledger.domain.model.LedgerJournal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LedgerJournalSnapshot(
        UUID id,
        String sourceCommandId,
        String idempotencyKey,
        String actorType,
        String actorId,
        String businessReason,
        LocalDate postingDate,
        LocalDate valueDate,
        String currency,
        String status,
        String correlationId,
        LocalDateTime postedAt,
        List<LedgerEntrySnapshot> entries
) {

    public static LedgerJournalSnapshot from(LedgerJournal journal) {
        return new LedgerJournalSnapshot(
                journal.id(),
                journal.sourceCommandId(),
                journal.idempotencyKey(),
                journal.actorType(),
                journal.actorId(),
                journal.businessReason(),
                journal.postingDate(),
                journal.valueDate(),
                journal.currency().getCurrencyCode(),
                journal.status().name(),
                journal.correlationId(),
                journal.postedAt(),
                journal.entries().stream()
                        .map(LedgerEntrySnapshot::from)
                        .toList()
        );
    }
}

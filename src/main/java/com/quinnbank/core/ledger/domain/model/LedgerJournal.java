package com.quinnbank.core.ledger.domain.model;

import com.quinnbank.core.common.domain.Money;
import com.quinnbank.core.ledger.domain.exception.LedgerPostingRejectedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class LedgerJournal {

    private final UUID id;
    private final String sourceCommandId;
    private final String idempotencyKey;
    private final String commandFingerprint;
    private final String actorType;
    private final String actorId;
    private final String businessReason;
    private final LocalDate postingDate;
    private final LocalDate valueDate;
    private final Currency currency;
    private final LedgerJournalStatus status;
    private final String correlationId;
    private final LocalDateTime createdAt;
    private final LocalDateTime postedAt;
    private final long version;
    private final List<LedgerEntry> entries;

    private LedgerJournal(
            UUID id,
            String sourceCommandId,
            String idempotencyKey,
            String commandFingerprint,
            String actorType,
            String actorId,
            String businessReason,
            LocalDate postingDate,
            LocalDate valueDate,
            Currency currency,
            LedgerJournalStatus status,
            String correlationId,
            LocalDateTime createdAt,
            LocalDateTime postedAt,
            long version,
            List<LedgerEntry> entries
    ) {
        this.id = id;
        this.sourceCommandId = sourceCommandId;
        this.idempotencyKey = idempotencyKey;
        this.commandFingerprint = commandFingerprint;
        this.actorType = actorType;
        this.actorId = actorId;
        this.businessReason = businessReason;
        this.postingDate = postingDate;
        this.valueDate = valueDate;
        this.currency = currency;
        this.status = status;
        this.correlationId = correlationId;
        this.createdAt = createdAt;
        this.postedAt = postedAt;
        this.version = version;
        this.entries = List.copyOf(entries);
    }

    public static LedgerJournal post(
            String sourceCommandId,
            String idempotencyKey,
            String commandFingerprint,
            String actorType,
            String actorId,
            String businessReason,
            LocalDate postingDate,
            LocalDate valueDate,
            Currency currency,
            String correlationId,
            List<LedgerEntryInput> entryInputs,
            LocalDateTime postedAt
    ) {
        validateJournalFields(
                sourceCommandId,
                idempotencyKey,
                commandFingerprint,
                actorType,
                actorId,
                businessReason,
                postingDate,
                valueDate,
                currency,
                correlationId,
                postedAt
        );

        List<LedgerEntry> entries = createEntries(entryInputs, currency, postedAt);
        verifyBalanced(entries);

        return new LedgerJournal(
                UUID.randomUUID(),
                sourceCommandId.trim(),
                idempotencyKey.trim(),
                commandFingerprint.trim(),
                actorType.trim().toUpperCase(),
                actorId.trim(),
                businessReason.trim(),
                postingDate,
                valueDate,
                currency,
                LedgerJournalStatus.POSTED,
                correlationId.trim(),
                postedAt,
                postedAt,
                0,
                entries
        );
    }

    public static LedgerJournal restore(
            UUID id,
            String sourceCommandId,
            String idempotencyKey,
            String commandFingerprint,
            String actorType,
            String actorId,
            String businessReason,
            LocalDate postingDate,
            LocalDate valueDate,
            Currency currency,
            LedgerJournalStatus status,
            String correlationId,
            LocalDateTime createdAt,
            LocalDateTime postedAt,
            long version,
            List<LedgerEntry> entries
    ) {
        if (id == null) {
            throw new IllegalArgumentException("ledger journal id is required");
        }
        validateJournalFields(
                sourceCommandId,
                idempotencyKey,
                commandFingerprint,
                actorType,
                actorId,
                businessReason,
                postingDate,
                valueDate,
                currency,
                correlationId,
                postedAt
        );
        if (status == null) {
            throw new IllegalArgumentException("ledger journal status is required");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("ledger journal creation time is required");
        }
        if (entries == null || entries.size() < 2) {
            throw new LedgerPostingRejectedException("ledger journal requires at least two entries");
        }
        for (LedgerEntry entry : entries) {
            if (!entry.amount().currency().equals(currency)) {
                throw new LedgerPostingRejectedException("ledger entry currency must match journal currency");
            }
        }
        verifyBalanced(entries);

        return new LedgerJournal(
                id,
                sourceCommandId.trim(),
                idempotencyKey.trim(),
                commandFingerprint.trim(),
                actorType.trim().toUpperCase(),
                actorId.trim(),
                businessReason.trim(),
                postingDate,
                valueDate,
                currency,
                status,
                correlationId.trim(),
                createdAt,
                postedAt,
                version,
                entries
        );
    }

    public boolean matchesFingerprint(String expectedFingerprint) {
        return commandFingerprint.equals(expectedFingerprint);
    }

    private static List<LedgerEntry> createEntries(
            List<LedgerEntryInput> entryInputs,
            Currency currency,
            LocalDateTime postedAt
    ) {
        if (entryInputs == null || entryInputs.size() < 2) {
            throw new LedgerPostingRejectedException("ledger journal requires at least two entries");
        }

        List<LedgerEntry> entries = new ArrayList<>();
        for (int index = 0; index < entryInputs.size(); index++) {
            LedgerEntry entry = LedgerEntry.create(index + 1, entryInputs.get(index), postedAt);
            if (!entry.amount().currency().equals(currency)) {
                throw new LedgerPostingRejectedException("ledger entry currency must match journal currency");
            }
            entries.add(entry);
        }
        return entries;
    }

    private static void verifyBalanced(List<LedgerEntry> entries) {
        Map<LedgerEntrySide, Money> totals = new EnumMap<>(LedgerEntrySide.class);
        for (LedgerEntry entry : entries) {
            Money currentTotal = totals.getOrDefault(entry.side(), Money.zero(entry.amount().currency()));
            totals.put(entry.side(), currentTotal.add(entry.amount()));
        }

        Money debitTotal = totals.getOrDefault(LedgerEntrySide.DEBIT, Money.zero(entries.get(0).amount().currency()));
        Money creditTotal = totals.getOrDefault(LedgerEntrySide.CREDIT, Money.zero(entries.get(0).amount().currency()));
        if (debitTotal.amount().compareTo(creditTotal.amount()) != 0) {
            throw new LedgerPostingRejectedException("ledger debits and credits must balance");
        }
        if (debitTotal.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new LedgerPostingRejectedException("ledger journal total must be positive");
        }
    }

    private static void validateJournalFields(
            String sourceCommandId,
            String idempotencyKey,
            String commandFingerprint,
            String actorType,
            String actorId,
            String businessReason,
            LocalDate postingDate,
            LocalDate valueDate,
            Currency currency,
            String correlationId,
            LocalDateTime postedAt
    ) {
        if (sourceCommandId == null || sourceCommandId.isBlank()) {
            throw new LedgerPostingRejectedException("source command id is required");
        }
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new LedgerPostingRejectedException("idempotency key is required");
        }
        if (idempotencyKey.length() > 120) {
            throw new LedgerPostingRejectedException("idempotency key is too long");
        }
        if (commandFingerprint == null || commandFingerprint.isBlank()) {
            throw new LedgerPostingRejectedException("command fingerprint is required");
        }
        if (actorType == null || actorType.isBlank()) {
            throw new LedgerPostingRejectedException("actor type is required");
        }
        if (actorId == null || actorId.isBlank()) {
            throw new LedgerPostingRejectedException("actor id is required");
        }
        if (businessReason == null || businessReason.isBlank()) {
            throw new LedgerPostingRejectedException("business reason is required");
        }
        if (postingDate == null) {
            throw new LedgerPostingRejectedException("posting date is required");
        }
        if (valueDate == null) {
            throw new LedgerPostingRejectedException("value date is required");
        }
        if (currency == null) {
            throw new LedgerPostingRejectedException("currency is required");
        }
        if (correlationId == null || correlationId.isBlank()) {
            throw new LedgerPostingRejectedException("correlation id is required");
        }
        if (postedAt == null) {
            throw new LedgerPostingRejectedException("posted time is required");
        }
    }

    public UUID id() {
        return id;
    }

    public String sourceCommandId() {
        return sourceCommandId;
    }

    public String idempotencyKey() {
        return idempotencyKey;
    }

    public String commandFingerprint() {
        return commandFingerprint;
    }

    public String actorType() {
        return actorType;
    }

    public String actorId() {
        return actorId;
    }

    public String businessReason() {
        return businessReason;
    }

    public LocalDate postingDate() {
        return postingDate;
    }

    public LocalDate valueDate() {
        return valueDate;
    }

    public Currency currency() {
        return currency;
    }

    public LedgerJournalStatus status() {
        return status;
    }

    public String correlationId() {
        return correlationId;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDateTime postedAt() {
        return postedAt;
    }

    public long version() {
        return version;
    }

    public List<LedgerEntry> entries() {
        return entries;
    }
}

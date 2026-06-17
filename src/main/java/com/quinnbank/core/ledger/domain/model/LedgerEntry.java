package com.quinnbank.core.ledger.domain.model;

import com.quinnbank.core.common.domain.Money;
import com.quinnbank.core.ledger.domain.exception.LedgerPostingRejectedException;

import java.time.LocalDateTime;
import java.util.UUID;

public final class LedgerEntry {

    private final UUID id;
    private final int lineNumber;
    private final UUID accountId;
    private final LedgerEntrySide side;
    private final Money amount;
    private final LocalDateTime createdAt;

    private LedgerEntry(
            UUID id,
            int lineNumber,
            UUID accountId,
            LedgerEntrySide side,
            Money amount,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.lineNumber = lineNumber;
        this.accountId = accountId;
        this.side = side;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    static LedgerEntry create(int lineNumber, LedgerEntryInput input, LocalDateTime createdAt) {
        if (input == null) {
            throw new LedgerPostingRejectedException("ledger entry is required");
        }

        return restore(UUID.randomUUID(), lineNumber, input.accountId(), input.side(), input.amount(), createdAt);
    }

    public static LedgerEntry restore(
            UUID id,
            int lineNumber,
            UUID accountId,
            LedgerEntrySide side,
            Money amount,
            LocalDateTime createdAt
    ) {
        if (id == null) {
            throw new IllegalArgumentException("ledger entry id is required");
        }
        if (lineNumber < 1) {
            throw new LedgerPostingRejectedException("ledger entry line number must be positive");
        }
        if (accountId == null) {
            throw new LedgerPostingRejectedException("ledger entry account id is required");
        }
        if (side == null) {
            throw new LedgerPostingRejectedException("ledger entry side is required");
        }
        if (amount == null || !amount.isPositive()) {
            throw new LedgerPostingRejectedException("ledger entry amount must be positive");
        }
        if (createdAt == null) {
            throw new LedgerPostingRejectedException("ledger entry creation time is required");
        }

        return new LedgerEntry(id, lineNumber, accountId, side, amount, createdAt);
    }

    public Money balanceDelta() {
        return side.balanceDelta(amount);
    }

    public UUID id() {
        return id;
    }

    public int lineNumber() {
        return lineNumber;
    }

    public UUID accountId() {
        return accountId;
    }

    public LedgerEntrySide side() {
        return side;
    }

    public Money amount() {
        return amount;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }
}

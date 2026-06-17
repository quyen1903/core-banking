package com.quinnbank.core.ledger.domain.model;

import com.quinnbank.core.common.domain.Money;

import java.util.UUID;

public record LedgerEntryInput(UUID accountId, LedgerEntrySide side, Money amount) {
}

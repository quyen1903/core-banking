package com.quinnbank.core.ledger.application.command;

import com.quinnbank.core.common.domain.Money;
import com.quinnbank.core.ledger.domain.model.LedgerEntrySide;

import java.util.UUID;

public record PostLedgerEntryCommand(UUID accountId, LedgerEntrySide side, Money amount) {
}

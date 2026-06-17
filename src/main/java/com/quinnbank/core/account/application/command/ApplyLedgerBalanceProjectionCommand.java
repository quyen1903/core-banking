package com.quinnbank.core.account.application.command;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ApplyLedgerBalanceProjectionCommand(
        UUID journalId,
        List<AccountBalanceDelta> deltas,
        LocalDateTime postedAt
) {
}

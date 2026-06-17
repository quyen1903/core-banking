package com.quinnbank.core.account.application.port.in;

import com.quinnbank.core.account.application.command.ApplyLedgerBalanceProjectionCommand;

public interface ApplyLedgerBalanceProjectionUseCase {

    void apply(ApplyLedgerBalanceProjectionCommand command);
}

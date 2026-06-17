package com.quinnbank.core.account.application.service;

import com.quinnbank.core.account.application.command.AccountBalanceDelta;
import com.quinnbank.core.account.application.command.ApplyLedgerBalanceProjectionCommand;
import com.quinnbank.core.account.application.exception.AccountNotFoundException;
import com.quinnbank.core.account.application.port.in.ApplyLedgerBalanceProjectionUseCase;
import com.quinnbank.core.account.application.port.out.AccountRepositoryPort;
import com.quinnbank.core.account.domain.exception.AccountBalanceProjectionRejectedException;
import com.quinnbank.core.account.domain.model.BankAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplyLedgerBalanceProjectionService implements ApplyLedgerBalanceProjectionUseCase {

    private final AccountRepositoryPort accountRepositoryPort;

    @Override
    @Transactional
    public void apply(ApplyLedgerBalanceProjectionCommand command) {
        validate(command);

        command.deltas().stream()
                .sorted(Comparator.comparing(delta -> delta.accountId().toString()))
                .forEach(delta -> applyDelta(command, delta));
    }

    private void applyDelta(ApplyLedgerBalanceProjectionCommand command, AccountBalanceDelta delta) {
        BankAccount account = accountRepositoryPort.findByIdForUpdate(delta.accountId())
                .orElseThrow(() -> AccountNotFoundException.byId(delta.accountId()));

        accountRepositoryPort.save(account.applyLedgerProjection(delta.delta(), command.postedAt()));
    }

    private static void validate(ApplyLedgerBalanceProjectionCommand command) {
        if (command == null) {
            throw new AccountBalanceProjectionRejectedException("ledger balance projection command is required");
        }
        if (command.journalId() == null) {
            throw new AccountBalanceProjectionRejectedException("ledger journal id is required");
        }
        if (command.postedAt() == null) {
            throw new AccountBalanceProjectionRejectedException("ledger posted time is required");
        }
        if (command.deltas() == null || command.deltas().isEmpty()) {
            throw new AccountBalanceProjectionRejectedException("ledger balance deltas are required");
        }

        Set<UUID> accountIds = new HashSet<>();
        for (AccountBalanceDelta delta : command.deltas()) {
            if (delta == null || delta.accountId() == null) {
                throw new AccountBalanceProjectionRejectedException("ledger balance delta account id is required");
            }
            if (delta.delta() == null || delta.delta().isZero()) {
                throw new AccountBalanceProjectionRejectedException("ledger balance delta amount is required");
            }
            if (!accountIds.add(delta.accountId())) {
                throw new AccountBalanceProjectionRejectedException("ledger balance deltas must be aggregated by account");
            }
        }
    }
}

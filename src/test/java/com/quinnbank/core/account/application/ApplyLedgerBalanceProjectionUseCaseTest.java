package com.quinnbank.core.account.application;

import com.quinnbank.core.account.application.command.AccountBalanceDelta;
import com.quinnbank.core.account.application.command.ApplyLedgerBalanceProjectionCommand;
import com.quinnbank.core.account.application.port.out.AccountRepositoryPort;
import com.quinnbank.core.account.application.service.ApplyLedgerBalanceProjectionService;
import com.quinnbank.core.account.domain.exception.AccountBalanceProjectionRejectedException;
import com.quinnbank.core.account.domain.model.AccountNumber;
import com.quinnbank.core.account.domain.model.AccountProduct;
import com.quinnbank.core.account.domain.model.BankAccount;
import com.quinnbank.core.common.domain.Money;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplyLedgerBalanceProjectionUseCaseTest {

    private static final Currency USD = Currency.getInstance("USD");

    private final AccountRepositoryPort accountRepositoryPort = mock(AccountRepositoryPort.class);
    private final ApplyLedgerBalanceProjectionService useCase =
            new ApplyLedgerBalanceProjectionService(accountRepositoryPort);

    @Test
    void applyLocksAccountsInDeterministicOrderAndPersistsProjectedBalances() {
        UUID firstAccountId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID secondAccountId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        BankAccount firstAccount = account(firstAccountId, "ACCT202606120000000001", "20.0000");
        BankAccount secondAccount = account(secondAccountId, "ACCT202606120000000002", "30.0000");
        when(accountRepositoryPort.findByIdForUpdate(firstAccountId)).thenReturn(Optional.of(firstAccount));
        when(accountRepositoryPort.findByIdForUpdate(secondAccountId)).thenReturn(Optional.of(secondAccount));
        when(accountRepositoryPort.save(any(BankAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.apply(new ApplyLedgerBalanceProjectionCommand(
                UUID.randomUUID(),
                List.of(
                        new AccountBalanceDelta(secondAccountId, money("7.0000")),
                        new AccountBalanceDelta(firstAccountId, money("5.0000"))
                ),
                LocalDateTime.of(2026, 6, 12, 2, 0)
        ));

        InOrder inOrder = inOrder(accountRepositoryPort);
        inOrder.verify(accountRepositoryPort).findByIdForUpdate(firstAccountId);
        inOrder.verify(accountRepositoryPort).save(any(BankAccount.class));
        inOrder.verify(accountRepositoryPort).findByIdForUpdate(secondAccountId);
        inOrder.verify(accountRepositoryPort).save(any(BankAccount.class));

        ArgumentCaptor<BankAccount> accountCaptor = ArgumentCaptor.forClass(BankAccount.class);
        verify(accountRepositoryPort, times(2)).save(accountCaptor.capture());
        assertThat(accountCaptor.getAllValues())
                .anySatisfy(account -> {
                    assertThat(account.id()).isEqualTo(firstAccountId);
                    assertThat(account.currentBalance().amount()).isEqualByComparingTo("25.0000");
                })
                .anySatisfy(account -> {
                    assertThat(account.id()).isEqualTo(secondAccountId);
                    assertThat(account.currentBalance().amount()).isEqualByComparingTo("37.0000");
                });
    }

    @Test
    void applyRejectsDuplicateDeltasForSameAccount() {
        UUID accountId = UUID.randomUUID();

        assertThatThrownBy(() -> useCase.apply(new ApplyLedgerBalanceProjectionCommand(
                UUID.randomUUID(),
                List.of(
                        new AccountBalanceDelta(accountId, money("5.0000")),
                        new AccountBalanceDelta(accountId, money("7.0000"))
                ),
                LocalDateTime.of(2026, 6, 12, 2, 0)
        )))
                .isInstanceOf(AccountBalanceProjectionRejectedException.class)
                .hasMessage("ledger balance deltas must be aggregated by account");

        verify(accountRepositoryPort, never()).findByIdForUpdate(any());
    }

    private static BankAccount account(UUID accountId, String accountNumber, String openingBalance) {
        AccountProduct product = AccountProduct.restore(
                UUID.randomUUID(),
                "DDA",
                "Everyday Checking",
                USD,
                Money.zero(USD),
                BigDecimal.ZERO,
                Money.zero(USD),
                true,
                LocalDateTime.of(2026, 6, 12, 1, 0),
                LocalDateTime.of(2026, 6, 12, 1, 0)
        );
        BankAccount zeroBalanceAccount = BankAccount.restore(
                accountId,
                new AccountNumber(accountNumber),
                UUID.randomUUID(),
                product.id(),
                Money.zero(USD),
                Money.zero(USD),
                com.quinnbank.core.account.domain.model.BankAccountStatus.OPEN,
                LocalDateTime.of(2026, 6, 12, 1, 0),
                null,
                "open-" + accountId,
                accountId + "|DDA",
                LocalDateTime.of(2026, 6, 12, 1, 0),
                LocalDateTime.of(2026, 6, 12, 1, 0),
                0
        );
        return zeroBalanceAccount.applyLedgerProjection(
                money(openingBalance),
                LocalDateTime.of(2026, 6, 12, 1, 30)
        );
    }

    private static Money money(String amount) {
        return new Money(new BigDecimal(amount), USD);
    }
}

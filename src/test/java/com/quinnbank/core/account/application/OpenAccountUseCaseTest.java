package com.quinnbank.core.account.application;

import com.quinnbank.core.account.application.command.OpenAccountCommand;
import com.quinnbank.core.account.application.exception.AccountOpeningConflictException;
import com.quinnbank.core.account.application.port.out.AccountNumberGeneratorPort;
import com.quinnbank.core.account.application.port.out.AccountProductLookupPort;
import com.quinnbank.core.account.application.port.out.AccountRepositoryPort;
import com.quinnbank.core.account.application.port.out.CustomerLookupPort;
import com.quinnbank.core.account.application.port.out.IdempotencyPort;
import com.quinnbank.core.account.application.result.AccountOpeningIdempotencyResult;
import com.quinnbank.core.account.application.result.AccountSnapshot;
import com.quinnbank.core.account.application.service.OpenAccountService;
import com.quinnbank.core.account.domain.model.AccountNumber;
import com.quinnbank.core.account.domain.model.AccountProduct;
import com.quinnbank.core.account.domain.model.BankAccount;
import com.quinnbank.core.account.domain.model.Money;
import com.quinnbank.core.account.domain.policy.AccountOpeningPolicy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpenAccountUseCaseTest {

    private final CustomerLookupPort customerLookupPort = mock(CustomerLookupPort.class);
    private final AccountProductLookupPort productLookupPort = mock(AccountProductLookupPort.class);
    private final AccountRepositoryPort accountRepositoryPort = mock(AccountRepositoryPort.class);
    private final IdempotencyPort idempotencyPort = mock(IdempotencyPort.class);
    private final AccountNumberGeneratorPort accountNumberGeneratorPort = mock(AccountNumberGeneratorPort.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-06-11T18:00:00Z"), ZoneOffset.UTC);
    private final OpenAccountService useCase = new OpenAccountService(
            customerLookupPort,
            productLookupPort,
            accountRepositoryPort,
            idempotencyPort,
            accountNumberGeneratorPort,
            new AccountOpeningPolicy(),
            clock
    );

    @Test
    void openCreatesAccountForActiveCustomerAndActiveProduct() {
        UUID customerId = UUID.randomUUID();
        AccountProduct product = product(BigDecimal.ZERO);
        when(idempotencyPort.findAccountOpening("idem-1")).thenReturn(Optional.empty());
        when(productLookupPort.findActiveOrInactiveByCode("DDA")).thenReturn(Optional.of(product));
        when(accountNumberGeneratorPort.nextAccountNumber()).thenReturn(new AccountNumber("ACCT202606120000000001"));
        when(accountRepositoryPort.save(any(BankAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountSnapshot account = useCase.open(new OpenAccountCommand(customerId, "dda", "idem-1"));

        assertThat(account.accountNumber()).isEqualTo("ACCT202606120000000001");
        assertThat(account.customerId()).isEqualTo(customerId);
        assertThat(account.productId()).isEqualTo(product.id());
        assertThat(account.currency()).isEqualTo("USD");
        assertThat(account.availableBalance()).isEqualByComparingTo("0.0000");
        assertThat(account.currentBalance()).isEqualByComparingTo("0.0000");
        verify(customerLookupPort).requireActiveCustomer(customerId);
    }

    @Test
    void openReturnsExistingAccountWhenIdempotencyKeyReplaysSameRequest() {
        UUID customerId = UUID.randomUUID();
        AccountProduct product = product(BigDecimal.ZERO);
        BankAccount existing = account(product, customerId, "idem-1", customerId + "|DDA");
        when(idempotencyPort.findAccountOpening("idem-1")).thenReturn(Optional.of(
                new AccountOpeningIdempotencyResult("idem-1", customerId + "|DDA", existing.id())
        ));
        when(accountRepositoryPort.findById(existing.id())).thenReturn(Optional.of(existing));

        AccountSnapshot replay = useCase.open(new OpenAccountCommand(customerId, "dda", "idem-1"));

        assertThat(replay.id()).isEqualTo(existing.id());
        verify(accountRepositoryPort, never()).save(any(BankAccount.class));
        verify(customerLookupPort, never()).requireActiveCustomer(any());
    }

    @Test
    void openRejectsSameIdempotencyKeyForDifferentRequest() {
        UUID firstCustomerId = UUID.randomUUID();
        UUID secondCustomerId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        when(idempotencyPort.findAccountOpening("idem-1")).thenReturn(Optional.of(
                new AccountOpeningIdempotencyResult("idem-1", firstCustomerId + "|DDA", accountId)
        ));

        assertThatThrownBy(() -> useCase.open(new OpenAccountCommand(secondCustomerId, "dda", "idem-1")))
                .isInstanceOf(AccountOpeningConflictException.class)
                .hasMessage("idempotency key was used for a different account opening request");
    }

    private static BankAccount account(
            AccountProduct product,
            UUID customerId,
            String idempotencyKey,
            String fingerprint
    ) {
        return BankAccount.open(
                product,
                customerId,
                new AccountNumber("ACCT202606120000000001"),
                idempotencyKey,
                fingerprint,
                LocalDateTime.of(2026, 6, 12, 1, 0)
        );
    }

    private static AccountProduct product(BigDecimal minBalance) {
        Currency currency = Currency.getInstance("USD");

        return AccountProduct.restore(
                UUID.randomUUID(),
                "dda",
                "Everyday Checking",
                currency,
                new Money(minBalance, currency),
                BigDecimal.ZERO,
                Money.zero(currency),
                true,
                LocalDateTime.of(2026, 6, 12, 1, 0),
                LocalDateTime.of(2026, 6, 12, 1, 0)
        );
    }
}

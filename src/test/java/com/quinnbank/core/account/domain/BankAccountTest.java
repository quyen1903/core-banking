package com.quinnbank.core.account.domain;

import com.quinnbank.core.account.domain.exception.AccountOpeningRejectedException;
import com.quinnbank.core.account.domain.model.AccountNumber;
import com.quinnbank.core.account.domain.model.AccountProduct;
import com.quinnbank.core.account.domain.model.AccountStatus;
import com.quinnbank.core.account.domain.model.BankAccount;
import com.quinnbank.core.account.domain.model.Money;
import com.quinnbank.core.account.domain.policy.AccountOpeningPolicy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountTest {

    @Test
    void openCreatesAccountWithZeroBalancesAndProductCurrency() {
        AccountProduct product = product(BigDecimal.ZERO);
        UUID customerId = UUID.randomUUID();
        LocalDateTime openedAt = LocalDateTime.of(2026, 6, 12, 1, 0);

        new AccountOpeningPolicy().verify(product);

        BankAccount account = BankAccount.open(
                product,
                customerId,
                new AccountNumber(" ACCT202606120000000001 "),
                " open-account-1 ",
                customerId + "|DDA",
                openedAt
        );

        assertThat(account.id()).isNotNull();
        assertThat(account.accountNumber().value()).isEqualTo("ACCT202606120000000001");
        assertThat(account.customerId()).isEqualTo(customerId);
        assertThat(account.productId()).isEqualTo(product.id());
        assertThat(account.availableBalance().currency().getCurrencyCode()).isEqualTo("USD");
        assertThat(account.availableBalance().amount()).isEqualByComparingTo("0.0000");
        assertThat(account.currentBalance().amount()).isEqualByComparingTo("0.0000");
        assertThat(account.status()).isEqualTo(AccountStatus.OPEN);
        assertThat(account.openingIdempotencyKey()).isEqualTo("open-account-1");
        assertThat(account.createdAt()).isEqualTo(openedAt);
        assertThat(account.updatedAt()).isEqualTo(openedAt);
        assertThat(account.pullDomainEvents()).hasSize(1);
    }

    @Test
    void openRejectsProductThatRequiresFunding() {
        AccountProduct product = product(new BigDecimal("100.0000"));

        assertThatThrownBy(() -> new AccountOpeningPolicy().verify(product))
                .isInstanceOf(AccountOpeningRejectedException.class)
                .hasMessage("account product requires funded opening");
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

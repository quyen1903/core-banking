package com.quinnbank.core.account.application.result;

import com.quinnbank.core.account.domain.model.AccountStatus;
import com.quinnbank.core.account.domain.model.BankAccount;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountSnapshot(
        UUID id,
        String accountNumber,
        UUID customerId,
        UUID productId,
        String currency,
        BigDecimal availableBalance,
        BigDecimal currentBalance,
        AccountStatus status
) {

    public static AccountSnapshot from(BankAccount account) {
        return new AccountSnapshot(
                account.id(),
                account.accountNumber().value(),
                account.customerId(),
                account.productId(),
                account.currentBalance().currency().getCurrencyCode(),
                account.availableBalance().amount(),
                account.currentBalance().amount(),
                account.status()
        );
    }
}

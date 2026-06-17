package com.quinnbank.core.account.application.result;

import com.quinnbank.core.account.domain.model.BankAccountStatus;
import com.quinnbank.core.account.domain.model.BankAccount;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountSnapshot(
        UUID id,
        String accountNumber,
        UUID customerId,
        UUID productId,
        String currency,
        BigDecimal availableBalance,
        BigDecimal currentBalance,
        BankAccountStatus status,
        LocalDateTime openedAt,
        LocalDateTime closedAt
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
                account.status(),
                account.openedAt(),
                account.closedAt()
        );
    }
}

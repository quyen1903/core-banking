package com.quinnbank.core.account.api.dto;

import com.quinnbank.core.account.application.result.AccountSnapshot;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String maskedAccountNumber,
        UUID customerId,
        UUID productId,
        String currency,
        BigDecimal availableBalance,
        BigDecimal currentBalance,
        String status,
        LocalDateTime openedAt,
        LocalDateTime closedAt
) {

    public static AccountResponse from(AccountSnapshot account) {
        return new AccountResponse(
                account.id(),
                maskAccountNumber(account.accountNumber()),
                account.customerId(),
                account.productId(),
                account.currency(),
                account.availableBalance(),
                account.currentBalance(),
                account.status().name(),
                account.openedAt(),
                account.closedAt()
        );
    }

    private static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return "****";
        }

        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
}

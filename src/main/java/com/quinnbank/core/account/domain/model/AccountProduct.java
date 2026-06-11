package com.quinnbank.core.account.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

public final class AccountProduct {

    private final UUID id;
    private final String code;
    private final String name;
    private final Currency currency;
    private final Money minBalance;
    private final BigDecimal interestRate;
    private final Money monthlyFee;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private AccountProduct(
            UUID id,
            String code,
            String name,
            Currency currency,
            Money minBalance,
            BigDecimal interestRate,
            Money monthlyFee,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.currency = currency;
        this.minBalance = minBalance;
        this.interestRate = interestRate;
        this.monthlyFee = monthlyFee;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AccountProduct restore(
            UUID id,
            String code,
            String name,
            Currency currency,
            Money minBalance,
            BigDecimal interestRate,
            Money monthlyFee,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        if (id == null) {
            throw new IllegalArgumentException("product id is required");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("product code is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("product name is required");
        }
        if (currency == null) {
            throw new IllegalArgumentException("currency is required");
        }
        if (minBalance == null) {
            throw new IllegalArgumentException("minimum balance is required");
        }
        if (interestRate == null || interestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("interest rate must be zero or positive");
        }
        if (monthlyFee == null) {
            throw new IllegalArgumentException("monthly fee is required");
        }
        if (createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("product timestamps are required");
        }

        return new AccountProduct(
                id,
                code.trim().toUpperCase(),
                name.trim(),
                currency,
                minBalance,
                interestRate,
                monthlyFee,
                active,
                createdAt,
                updatedAt
        );
    }

    public UUID id() {
        return id;
    }

    public String code() {
        return code;
    }

    public String name() {
        return name;
    }

    public Currency currency() {
        return currency;
    }

    public Money minBalance() {
        return minBalance;
    }

    public BigDecimal interestRate() {
        return interestRate;
    }

    public Money monthlyFee() {
        return monthlyFee;
    }

    public boolean active() {
        return active;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }

    public boolean permitsZeroOpeningBalance() {
        return minBalance.isZeroOrNegative();
    }
}

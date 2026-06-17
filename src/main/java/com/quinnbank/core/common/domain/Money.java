package com.quinnbank.core.common.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) {

    private static final int ACCOUNTING_SCALE = 4;
    private static final int MAX_PRECISION = 19;

    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("amount is required");
        }
        if (currency == null) {
            throw new IllegalArgumentException("currency is required");
        }

        amount = amount.setScale(ACCOUNTING_SCALE, RoundingMode.UNNECESSARY);
        if (amount.precision() > MAX_PRECISION) {
            throw new IllegalArgumentException("amount precision exceeds accounting limit");
        }
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO.setScale(ACCOUNTING_SCALE), currency);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    public Money negate() {
        return new Money(amount.negate(), currency);
    }

    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isZeroOrNegative() {
        return amount.compareTo(BigDecimal.ZERO) <= 0;
    }

    public void requireSameCurrency(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("money value is required");
        }
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("money currencies must match");
        }
    }
}

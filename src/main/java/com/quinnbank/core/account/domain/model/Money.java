package com.quinnbank.core.account.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) {

    private static final int ACCOUNTING_SCALE = 4;

    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("amount is required");
        }
        if (currency == null) {
            throw new IllegalArgumentException("currency is required");
        }

        amount = amount.setScale(ACCOUNTING_SCALE, RoundingMode.UNNECESSARY);
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO.setScale(ACCOUNTING_SCALE), currency);
    }

    public boolean isZeroOrNegative() {
        return amount.compareTo(BigDecimal.ZERO) <= 0;
    }
}

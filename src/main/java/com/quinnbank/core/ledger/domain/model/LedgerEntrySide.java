package com.quinnbank.core.ledger.domain.model;

import com.quinnbank.core.common.domain.Money;

public enum LedgerEntrySide {
    DEBIT {
        @Override
        public Money balanceDelta(Money amount) {
            return amount.negate();
        }
    },
    CREDIT {
        @Override
        public Money balanceDelta(Money amount) {
            return amount;
        }
    };

    public abstract Money balanceDelta(Money amount);
}

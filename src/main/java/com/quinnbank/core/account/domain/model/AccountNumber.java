package com.quinnbank.core.account.domain.model;

public record AccountNumber(String value) {

    public AccountNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("account number is required");
        }

        value = value.trim();
    }

    public String masked() {
        if (value.length() <= 4) {
            return "****";
        }

        return "****" + value.substring(value.length() - 4);
    }
}

package com.quinnbank.core.cif.application;

public record RegisterCustomerCommand(
        String fullName,
        String email,
        String phone
) {
}

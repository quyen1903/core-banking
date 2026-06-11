package com.quinnbank.core.customer.application;

public record CreateCustomerCommand(
        String fullName,
        String email,
        String phone
) {
}

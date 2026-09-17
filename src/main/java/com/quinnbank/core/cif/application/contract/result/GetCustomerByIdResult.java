package com.quinnbank.core.cif.application.contract.result;

public record GetCustomerByIdResult(
    String customerId,
    String firstName,
    String lastName,
    String email,
    String phoneNumber
) {}

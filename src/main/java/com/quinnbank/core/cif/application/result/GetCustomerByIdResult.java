package com.quinnbank.core.cif.application.result;

import java.util.UUID;

/** Structured names can be absent on legacy rows; fullName preserves their recorded name. */
public record GetCustomerByIdResult(
    UUID customerId,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String fullName
) {}

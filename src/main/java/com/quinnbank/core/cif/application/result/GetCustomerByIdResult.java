package com.quinnbank.core.cif.application.result;

import java.util.UUID;

/** Customer details with a display name composed from the available first and last names. */
public record GetCustomerByIdResult(
    UUID customerId,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String fullName
) {}

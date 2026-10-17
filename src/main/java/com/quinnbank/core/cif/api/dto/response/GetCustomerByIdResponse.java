package com.quinnbank.core.cif.api.dto.response;

import java.util.UUID;

/** Full name is composed from the available first and last names, or null when both are absent. */
public record GetCustomerByIdResponse(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String fullName
) {}

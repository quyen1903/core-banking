package com.quinnbank.core.cif.api.dto.request;

public record UpdateCustomerRequest(
    String firstName,
    String lastName,
    String email,
    String phone
) {}

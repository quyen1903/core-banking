package com.quinnbank.core.cif.api.dto.response;

import java.util.UUID;

/** Full name remains available when legacy records have no structured name components. */
public record GetCustomerByIdResponse(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String fullName
) {}

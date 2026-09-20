package com.quinnbank.core.cif.api.dto.response;

import java.util.UUID;

public record RegisterCustomerResponse(
    UUID id,
    String status
) {}

package com.quinnbank.core.cif.adapter.in.dto.response;

import java.util.UUID;

public record RegisterCustomerResponse(
    UUID id,
    String status
) {}

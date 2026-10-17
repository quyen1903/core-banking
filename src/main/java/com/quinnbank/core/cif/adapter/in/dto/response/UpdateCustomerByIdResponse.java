package com.quinnbank.core.cif.adapter.in.dto.response;

import java.util.UUID;

public record UpdateCustomerByIdResponse(
    UUID id,
    String status
) {

}

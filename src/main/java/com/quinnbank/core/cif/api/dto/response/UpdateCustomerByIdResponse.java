package com.quinnbank.core.cif.api.dto.response;

import java.util.UUID;

public record UpdateCustomerByIdResponse(
    UUID id,
    String status
) {

}

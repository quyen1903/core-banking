package com.quinnbank.core.cif.api.mapper;

import com.quinnbank.core.cif.api.dto.request.RegisterCustomerRequest;
import com.quinnbank.core.cif.api.dto.response.GetCustomerByIdResponse;
import com.quinnbank.core.cif.api.dto.response.RegisterCustomerResponse;
import com.quinnbank.core.cif.application.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.query.GetCustomerByIdQuery;
import com.quinnbank.core.cif.application.result.GetCustomerByIdResult;
import com.quinnbank.core.cif.application.result.RegisterCustomerResult;

import java.util.UUID;

public final class CustomerHttpMapper {

    private CustomerHttpMapper() {
    }

    public static RegisterCustomerCommand toCommand(RegisterCustomerRequest request) {
        return new RegisterCustomerCommand(
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phone(),
            request.officeId(),
            request.externalId()
        );
    }

    public static RegisterCustomerResponse toResponse(RegisterCustomerResult result) {
        return new RegisterCustomerResponse(result.customerId(), result.status());
    }

    public static GetCustomerByIdQuery toQuery(UUID customerId) {
        return new GetCustomerByIdQuery(customerId);
    }

    public static GetCustomerByIdResponse toResponse(GetCustomerByIdResult result) {
        return new GetCustomerByIdResponse(result.customerId(), result.firstName(), result.lastName(),
                result.email(), result.phoneNumber(), result.fullName());
    }
}

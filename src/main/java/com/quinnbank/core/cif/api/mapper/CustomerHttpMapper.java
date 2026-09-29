package com.quinnbank.core.cif.api.mapper;

import java.util.UUID;

import com.quinnbank.core.cif.api.dto.request.RegisterCustomerRequest;
import com.quinnbank.core.cif.api.dto.request.UpdateCustomerRequest;
import com.quinnbank.core.cif.api.dto.response.UpdateCustomerByIdResponse;
import com.quinnbank.core.cif.api.dto.response.GetCustomerByIdResponse;
import com.quinnbank.core.cif.api.dto.response.RegisterCustomerResponse;
import com.quinnbank.core.cif.application.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.command.UpdateCustomerCommand;
import com.quinnbank.core.cif.application.query.GetCustomerByIdQuery;
import com.quinnbank.core.cif.application.result.GetCustomerByIdResult;
import com.quinnbank.core.cif.application.result.RegisterCustomerResult;
import com.quinnbank.core.cif.application.result.UpdateCustomerResult;

//convert data between HTTP layer and application layer
public final class CustomerHttpMapper {

    private CustomerHttpMapper() {}

    public static RegisterCustomerCommand toRegisterCommand(RegisterCustomerRequest request) {
        return new RegisterCustomerCommand(
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phone(),
            request.officeId(),
            request.externalId()
        );
    }

    public static UpdateCustomerCommand toUpdateCommand(UUID id,UpdateCustomerRequest request) {
        return new UpdateCustomerCommand(
            id,
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phone()
        );
    }


    public static RegisterCustomerResponse RegisterCustomerResponse(RegisterCustomerResult result) {
        return new RegisterCustomerResponse(result.customerId(), result.status());
    }

    public static UpdateCustomerByIdResponse UpdateCustomerByIdResponse(UpdateCustomerResult result) {
        return new UpdateCustomerByIdResponse(result.customerId(), result.status());
    }

    public static GetCustomerByIdQuery toQuery(UUID customerId) {
        return new GetCustomerByIdQuery(customerId);
    }

    public static GetCustomerByIdResponse toResponse(GetCustomerByIdResult result) {
        return new GetCustomerByIdResponse(result.customerId(), result.firstName(), result.lastName(),
                result.email(), result.phoneNumber(), result.fullName());
    }
}

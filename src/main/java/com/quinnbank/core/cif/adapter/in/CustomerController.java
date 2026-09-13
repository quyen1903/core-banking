package com.quinnbank.core.cif.adapter.in;

import com.quinnbank.core.cif.application.contract.result.RegisterCustomerResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quinnbank.core.cif.adapter.in.response.RegisterCustomerResponse;
import com.quinnbank.core.cif.application.contract.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.port.in.RegisterCustomerUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final RegisterCustomerUseCase registerCustomerUseCase;

    @PostMapping
    public RegisterCustomerResponse registerCustomer(
        @Valid
        @RequestBody
        RegisterCustomerCommand request
    ) {


        RegisterCustomerCommand command = new RegisterCustomerCommand(
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phone(),
            request.officeId(),
            request.externalId()
        );

        RegisterCustomerResult result = registerCustomerUseCase.registerCustomer(command);

        return RegisterCustomerResponse.from(result);
    }

    // @GetMapping("/{customerId}")
    // @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    // public CustomerResponse getCustomer(@PathVariable UUID customerId) {
    //     return CustomerResponse.from(getCustomerProfileQuery.findById(customerId));
    // }
}

package com.quinnbank.core.cif.api;

import com.quinnbank.core.cif.api.dto.CustomerResponse;
import com.quinnbank.core.cif.api.dto.RegisterCustomerRequest;
import com.quinnbank.core.cif.application.CustomerSnapshot;
import com.quinnbank.core.cif.application.GetCustomerProfileQuery;
import com.quinnbank.core.cif.application.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.RegisterCustomerUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final RegisterCustomerUseCase registerCustomerUseCase;
    private final GetCustomerProfileQuery getCustomerProfileQuery;

    @PostMapping
    public CustomerResponse registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
        CustomerSnapshot customer = registerCustomerUseCase.register(
                new RegisterCustomerCommand(
                        request.fullName(),
                        request.email(),
                        request.phone()
                )
        );

        return CustomerResponse.from(customer);
    }

    @GetMapping("/{customerId}")
    public CustomerResponse getCustomer(@PathVariable UUID customerId) {
        return CustomerResponse.from(getCustomerProfileQuery.findById(customerId));
    }
}

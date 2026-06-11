package com.quinnbank.core.customer.api;

import com.quinnbank.core.customer.application.CreateCustomerCommand;
import com.quinnbank.core.customer.application.CustomerService;
import com.quinnbank.core.customer.domain.Customer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public CustomerResponse createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        Customer customer = customerService.createCustomer(
                new CreateCustomerCommand(
                        request.fullName(),
                        request.email(),
                        request.phone()
                )
        );

        return CustomerResponse.from(customer);
    }

    public record CreateCustomerRequest(
            @NotBlank String fullName,
            String email,
            String phone
    ) {
    }

    public record CustomerResponse(
            UUID id,
            String customerNumber,
            String fullName,
            String email,
            String phone,
            String status
    ) {
        public static CustomerResponse from(Customer customer) {
            return new CustomerResponse(
                    customer.getId(),
                    customer.getCustomerNumber(),
                    customer.getFullName(),
                    customer.getEmail(),
                    customer.getPhone(),
                    customer.getStatus().name()
            );
        }
    }
}
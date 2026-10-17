package com.quinnbank.core.cif.application.command;

import com.quinnbank.core.cif.domain.model.customer.Customer;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdateCustomerCommand(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String phone
) {
    public UpdateCustomerCommand {
        email = Customer.normalizeEmail(email);
    }
}

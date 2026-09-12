package com.quinnbank.core.cif.application.contract.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterCustomerCommand(
        @NotBlank
        @Size(max = 255)
        String firstName,

        @NotBlank
        @Size(max = 255)
        String lastName,

        @Email
        @Size(max = 255)
        String email,

        @Size(max = 50)
        String phone,
        
        @NotBlank 
        String officeId,

        String externalId
) {
}

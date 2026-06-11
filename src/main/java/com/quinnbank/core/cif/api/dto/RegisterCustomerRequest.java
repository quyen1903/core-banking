package com.quinnbank.core.cif.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterCustomerRequest(
        @NotBlank
        @Size(max = 255)
        String fullName,

        @Email
        @Size(max = 255)
        String email,

        @Size(max = 50)
        String phone
) {
}

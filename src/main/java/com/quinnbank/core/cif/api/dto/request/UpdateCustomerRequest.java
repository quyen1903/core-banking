package com.quinnbank.core.cif.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(
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
    String phone
) {}

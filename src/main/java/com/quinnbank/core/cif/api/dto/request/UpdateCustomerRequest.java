package com.quinnbank.core.cif.api.dto.request;

import com.quinnbank.core.cif.api.annotation.NullOrNotBlank;
import jakarta.validation.constraints.Email;

public record UpdateCustomerRequest(
    @NullOrNotBlank
    @Size(max = 255)
    String firstName,
    
    @NullOrNotBlank 
    @Size(max = 255)
    String lastName,

    @NullOrNotBlank 
    @Email(message = "Email should be valid")
    @Size(max = 255)
    String email,

    @NullOrNotBlank
    @Size(max = 50)
    String phone
) {}

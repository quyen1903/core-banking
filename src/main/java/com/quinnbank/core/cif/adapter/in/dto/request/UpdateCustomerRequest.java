package com.quinnbank.core.cif.adapter.in.dto.request;

import com.quinnbank.core.cif.adapter.in.annotation.NullOrNotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

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

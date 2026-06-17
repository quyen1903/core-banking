package com.quinnbank.core.employee.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateEmployeeRequest(
        @NotBlank
        @Size(max = 255)
        String fullName,

        @NotBlank
        @Email
        @Size(max = 255)
        String workEmail,

        @Size(max = 120)
        String jobTitle,

        @NotBlank
        @Size(max = 50)
        @Pattern(regexp = "^[A-Za-z0-9_-]+$")
        String branchCode
) {
}

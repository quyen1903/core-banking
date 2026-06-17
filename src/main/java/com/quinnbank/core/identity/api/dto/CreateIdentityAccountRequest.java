package com.quinnbank.core.identity.api.dto;

import com.quinnbank.core.identity.domain.model.IdentitySubjectType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateIdentityAccountRequest(
        @NotBlank
        @Size(max = 120)
        String username,

        @Email
        @Size(max = 255)
        String email,

        @NotNull
        IdentitySubjectType subjectType,

        UUID subjectId,

        @NotBlank
        @Size(min = 12, max = 128)
        String initialPassword
) {
}

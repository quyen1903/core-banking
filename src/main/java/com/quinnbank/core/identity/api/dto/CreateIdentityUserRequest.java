package com.quinnbank.core.identity.api.dto;

import com.quinnbank.core.identity.domain.model.IdentityOwnerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateIdentityUserRequest(
        @NotNull
        IdentityOwnerType ownerType,

        @NotNull
        @Positive
        Long ownerId,

        @NotBlank
        @Size(max = 120)
        String username,

        @Email
        @Size(max = 255)
        String email,

        @Size(max = 50)
        String phoneNumber
) {
}

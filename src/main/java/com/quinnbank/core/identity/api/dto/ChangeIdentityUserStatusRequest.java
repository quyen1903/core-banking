package com.quinnbank.core.identity.api.dto;

import com.quinnbank.core.identity.domain.model.IdentityUserStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeIdentityUserStatusRequest(
        @NotNull
        IdentityUserStatus status
) {
}

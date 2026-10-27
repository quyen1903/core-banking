package com.quinnbank.core.auth.adapter.in.dto.request;

import com.quinnbank.core.auth.domain.enums.IdentityOwnerType;
import java.util.UUID;

public record RegisterUserRequest(
    String username,
    String password,
    IdentityOwnerType ownerType,
    UUID ownerId
) {}

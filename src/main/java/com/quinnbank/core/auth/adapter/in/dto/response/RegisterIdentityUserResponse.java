package com.quinnbank.core.auth.adapter.in.dto.response;

import java.util.UUID;

import com.quinnbank.core.auth.domain.enums.IdentityStatus;

public record RegisterIdentityUserResponse(
    UUID publicUserId,
    IdentityStatus status
) {}

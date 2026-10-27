package com.quinnbank.core.auth.adapter.in.dto.response;

import java.util.UUID;

public record RegisterIdentityUserResponse(
    UUID publicUserId,
    String status
) {}

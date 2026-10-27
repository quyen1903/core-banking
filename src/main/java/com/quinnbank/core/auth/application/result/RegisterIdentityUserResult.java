package com.quinnbank.core.auth.application.result;

import com.quinnbank.core.auth.domain.enums.IdentityStatus;
import java.util.UUID;

public record RegisterIdentityUserResult(
    UUID publicUserId,
    IdentityStatus status
) {}

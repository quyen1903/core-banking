package com.quinnbank.core.auth.application.result;

import java.util.UUID;

public record LoginIdentityUserResult(
    UUID publicUserId,
    String status
) {}

package com.quinnbank.core.identity.application.command;

import java.util.UUID;

public record SetIdentityPasswordCommand(
        UUID publicId,
        String newPassword,
        boolean mustChangePassword
) {
}

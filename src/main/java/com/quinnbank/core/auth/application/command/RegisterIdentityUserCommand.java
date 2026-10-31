package com.quinnbank.core.auth.application.command;

import java.util.UUID;

import com.quinnbank.core.auth.domain.enums.IdentityOwnerType;

public record RegisterIdentityUserCommand(
    String username,
    String password,
    IdentityOwnerType ownerType,
    UUID ownerId
) {
    public String toString(){
        return "RegisterIdentityUserCommand[redacted]";
    }
}

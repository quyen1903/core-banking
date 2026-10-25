package com.quinnbank.core.auth.application.command;

import com.quinnbank.core.auth.domain.enums.IdentityOwnerType;

public record RegisterIdentityUserCommand(
    String username,
    String password,
    IdentityOwnerType ownerType
) {
    public String toString(){
        return "RegisterIdentityUserCommand[redacted]";
    }
}

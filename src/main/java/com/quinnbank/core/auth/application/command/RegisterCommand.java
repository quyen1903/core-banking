package com.quinnbank.core.auth.application.command;

import com.quinnbank.core.auth.domain.enums.IdentityOwnerType;

public record RegisterCommand(
    String username,
    String password,
    IdentityOwnerType ownerType
) {
    public String toString(){
        return "RegisterCommand[redacted]";
    }
}

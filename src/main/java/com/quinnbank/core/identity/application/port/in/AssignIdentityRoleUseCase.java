package com.quinnbank.core.identity.application.port.in;

import com.quinnbank.core.identity.application.command.AssignIdentityRoleCommand;

public interface AssignIdentityRoleUseCase {

    void assignRole(AssignIdentityRoleCommand command);
}

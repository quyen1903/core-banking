package com.quinnbank.core.identity.application.port.in;

import com.quinnbank.core.identity.application.command.RemoveIdentityRoleCommand;

public interface RemoveIdentityRoleUseCase {

    void removeRole(RemoveIdentityRoleCommand command);
}

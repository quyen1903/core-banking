package com.quinnbank.core.identity.application.port.in;

import com.quinnbank.core.identity.application.command.ChangeIdentityUserStatusCommand;
import com.quinnbank.core.identity.application.result.IdentityUserSnapshot;

public interface ChangeIdentityUserStatusUseCase {

    IdentityUserSnapshot changeStatus(ChangeIdentityUserStatusCommand command);
}

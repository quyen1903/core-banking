package com.quinnbank.core.identity.application.port.in;

import com.quinnbank.core.identity.application.command.CreateIdentityUserCommand;
import com.quinnbank.core.identity.application.result.IdentityUserSnapshot;

public interface CreateIdentityUserUseCase {

    IdentityUserSnapshot create(CreateIdentityUserCommand command);
}

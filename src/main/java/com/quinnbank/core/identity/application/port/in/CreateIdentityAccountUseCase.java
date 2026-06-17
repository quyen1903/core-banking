package com.quinnbank.core.identity.application.port.in;

import com.quinnbank.core.identity.application.command.CreateIdentityAccountCommand;
import com.quinnbank.core.identity.application.result.IdentityAccountSnapshot;

public interface CreateIdentityAccountUseCase {

    IdentityAccountSnapshot create(CreateIdentityAccountCommand command);
}

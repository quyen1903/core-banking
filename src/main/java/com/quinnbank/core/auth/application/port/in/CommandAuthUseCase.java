package com.quinnbank.core.auth.application.port.in;

import com.quinnbank.core.auth.application.command.RegisterIdentityUserCommand;
import com.quinnbank.core.auth.application.result.RegisterIdentityUserResult;

public interface CommandAuthUseCase {
    RegisterIdentityUserResult register(RegisterIdentityUserCommand command);
}

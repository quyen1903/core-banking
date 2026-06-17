package com.quinnbank.core.identity.application.port.in;

import com.quinnbank.core.identity.application.command.SetIdentityPasswordCommand;

public interface SetIdentityPasswordUseCase {

    void setPassword(SetIdentityPasswordCommand command);
}

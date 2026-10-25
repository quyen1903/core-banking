package com.quinnbank.core.auth.application.service;

import java.time.Instant;

import org.springframework.transaction.annotation.Transactional;

import com.quinnbank.core.auth.application.command.RegisterIdentityUserCommand;
import com.quinnbank.core.auth.application.port.out.IdentityUserWritePort;
import com.quinnbank.core.auth.application.port.out.PasswordHashingPort;
import com.quinnbank.core.auth.application.result.RegisterIdentityUserResult;
import com.quinnbank.core.auth.domain.model.IdentityUser;
import com.quinnbank.core.auth.domain.model.PasswordCredential;


/**
 * 
 * AuthCommandService
 * 1Verify the registration request and identify the customer permitted to link
 * 1 Normalize and validate username
 * 2 Check if username is already in use
 * 3 Call IdentityUser.create(...) to create identity
 * 4 Hash password using password encoder
 * 5 Create credential containing password hash
 * 6 Save identity, credential and audit in the same transaction
 * 7 Return safe result to client
 */
@Transactional(rollbackFor = Exception.class)
public class AuthCommandService {
    IdentityUserWritePort identityUserWritePort;
    PasswordHashingPort passwordHashingPort;

    AuthCommandService(
        IdentityUserWritePort identityUserWritePort,
        PasswordHashingPort passwordHashingPort
    ) {
        this.identityUserWritePort = identityUserWritePort;
        this.passwordHashingPort = passwordHashingPort;
    }

    public RegisterIdentityUserResult register(RegisterIdentityUserCommand command) {
        // Registration logic here
        //1 Normalize and validate username
        String normalizedUsername = IdentityUser.normalizeUsername(command.username());

        //2 Check if username is already in use
        if (normalizedUsername == null || identityUserWritePort.existsByUsernameNormalized(normalizedUsername)) {
            throw new IllegalArgumentException("Username is required");
        }

        //3 Call IdentityUser.create(...) to create identity
        IdentityUser identityUser = IdentityUser.create(normalizedUsername, command.ownerType());

        //4 Hash password using password encoder
        String passwordHash = passwordHashingPort.encode(command.password());

        //5 Create credential containing password hash
        PasswordCredential credential = PasswordCredential.create(identityUser.getId(), passwordHash, Instant.now(), false);

        //6 Save identity, credential and audit in the same transaction
        identityUserWritePort.save(identityUser);
        return new RegisterIdentityUserResult();
    }
}

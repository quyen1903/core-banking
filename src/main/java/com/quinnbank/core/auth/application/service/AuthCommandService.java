package com.quinnbank.core.auth.application.service;

import java.util.Optional;

import com.quinnbank.core.auth.application.command.RegisterIdentityUserCommand;
import com.quinnbank.core.auth.application.port.in.CommandAuthUseCase;
import com.quinnbank.core.auth.application.port.out.IdentityCredentialWritePort;
import com.quinnbank.core.auth.application.port.out.IdentityLoginStateWritePort;
import com.quinnbank.core.auth.application.port.out.IdentityUserWritePort;
import com.quinnbank.core.auth.application.port.out.PasswordHashingPort;
import com.quinnbank.core.auth.application.result.LoginIdentityUserResult;
import com.quinnbank.core.auth.application.result.RegisterIdentityUserResult;
import com.quinnbank.core.auth.domain.enums.IdentityStatus;
import com.quinnbank.core.auth.domain.model.IdentityCredential;
import com.quinnbank.core.auth.domain.model.IdentityLoginState;

public final class AuthCommandService implements CommandAuthUseCase {
    private final IdentityUserWritePort identityUserWritePort;
    private final IdentityCredentialWritePort  identityCredentialWritePort;
    private final IdentityLoginStateWritePort identityLoginStateWritePort;
    private final PasswordHashingPort passwordHashingPort;

    public AuthCommandService(
        IdentityUserWritePort identityUserWritePort,
        IdentityCredentialWritePort identityCredentialWritePort,
        IdentityLoginStateWritePort identityLoginStateWritePort,
        PasswordHashingPort passwordHashingPort
    ) {
        this.identityUserWritePort = identityUserWritePort;
        this.identityCredentialWritePort = identityCredentialWritePort;
        this.identityLoginStateWritePort = identityLoginStateWritePort;
        this.passwordHashingPort = passwordHashingPort;
    }

    /**
     * Registers a new identity user.
     *
     * @param command the registration command
     * @return the registration result
     *
     * 1 Normalize and validate username
     * 2 Check if username is already in use
     * 3 Call IdentityUser.create(...) to create identity
     * 4 Hash password using password encoder
     * 5 Create credential containing password hash
     * 6 Save identity, credential and audit in the same transaction
     * 7 Return safe result to client
     */
    @Override
    public RegisterIdentityUserResult register(RegisterIdentityUserCommand command) {
        // Registration logic here
        //1 Normalize and validate username
        String normalizedUsername = IdentityUser.normalizeUsername(command.username());

        //2 Check if username is already in use
        if (normalizedUsername == null || identityUserWritePort.existsByUsernameNormalized(normalizedUsername)) {
            throw new IllegalArgumentException("Username is required");
        }

        //3 Call IdentityUser.create(...) to create identity
        IdentityUser identityUser = IdentityUser.create(normalizedUsername, command.ownerType(), command.ownerId());

        //4 Hash password using password encoder
        String passwordHash = passwordHashingPort.encode(command.password());

        //5 Create credential containing password hash and login state
        IdentityCredential credential = IdentityCredential.create(identityUser.getId(), identityUser.getPublicId(), passwordHash, false);
        IdentityLoginState loginState = IdentityLoginState.create(identityUser.getId());

        //6 Save identity, credential and audit in the same transaction
        identityUserWritePort.save(identityUser);
        identityCredentialWritePort.save(credential);
        identityLoginStateWritePort.save(loginState);
        
        return new RegisterIdentityUserResult(identityUser.getId(), identityUser.getStatus());
    }

    /**
     * Logs in an identity user.
     *
     * @param username the submitted username
     * @param password the submitted password
     * @return the authentication result or required next step
     *
     * 1 Normalize and validate username.
     * 2 Retrieve identity user, credential, and login state.
     * 3 Check identity status, owner eligibility, and temporary login lock.
     * 4 Verify password through PasswordHashingPort.
     * 5 On failure, record the failed attempt and audit event,
     *   then return a safe rejection.
     * 6 If MFA or password change is required, return the required next step
     *   without granting business access.
     * 7 When all authentication requirements are satisfied, recheck security
     *   state and credential versions, update login state, and save the
     *   success audit event in a short transaction.
     * 8 Return a safe application result without exposing credentials.
     *
     * Password verification must not hold database locks.
     * Failed-attempt updates and audit events must survive login rejection.
     *
     * The Spring Security adapter handles session ID rotation,
     * SecurityContext persistence, and cookies after full authentication.
     */

    public LoginIdentityUserResult login(String username, String password) {
        // Login logic here
        //1 Normalize and validate username
        String normalizedUsername = IdentityUser.normalizeUsername(username);

        //2 Check if username exists
        if (normalizedUsername == null || !identityUserWritePort.existsByUsernameNormalized(normalizedUsername)) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        //3 Retrieve user 
        Optional<IdentityUser> identityUserOpt = identityUserWritePort.findByUsernameNormalized(normalizedUsername);
        if (identityUserOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        IdentityUser identityUser = identityUserOpt.get();

        //4 Retrieve credential
        Optional<IdentityCredential> credentialOpt = identityCredentialWritePort.findByUserId(identityUser.getId());
        if (credentialOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        IdentityCredential credential = credentialOpt.get();

        //6 Check identity status
        if (identityUser.getStatus() != IdentityStatus.ACTIVE) {
            throw new IllegalArgumentException("Identity user is not active");
        }

        //7 Verify password
        if (!passwordHashingPort.matches(password, credential.getPasswordHash())) {
            
            throw new IllegalArgumentException("Invalid username or password");
        }



        return new LoginIdentityUserResult(identityUser.getId(), identityUser.getStatus());
    }
}

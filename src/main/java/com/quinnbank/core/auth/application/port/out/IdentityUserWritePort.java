package com.quinnbank.core.auth.application.port.out;

public interface IdentityUserWritePort {
    boolean existsByUsernameNormalized(String usernameNormalized);
}

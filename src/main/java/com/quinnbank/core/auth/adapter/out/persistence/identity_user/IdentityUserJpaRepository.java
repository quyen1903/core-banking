package com.quinnbank.core.auth.adapter.out.persistence.identity_user;

public interface IdentityUserJpaRepository {
    boolean existsByUsernameNormalized(String usernameNormalized);
}

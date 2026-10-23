package com.quinnbank.core.auth.adapter.out.persistence.identity_user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityUserJpaRepository  extends JpaRepository<IdentityUserJpaEntity, UUID> {
    boolean existsByUsernameNormalized(String usernameNormalized);
}

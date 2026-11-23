package com.quinnbank.core.auth.adapter.out.persistence.identity_login_state;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IdentityLoginStateJpaRepository extends JpaRepository<UUID, IdentityLoginStateJpaRepository> {}

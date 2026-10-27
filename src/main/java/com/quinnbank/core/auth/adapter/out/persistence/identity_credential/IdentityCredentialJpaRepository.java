package com.quinnbank.core.auth.adapter.out.persistence.identity_credential;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityCredentialJpaRepository extends JpaRepository<IdentityCredentialJpaEntity, UUID> {

}

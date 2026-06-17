package com.quinnbank.core.identity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataIdentityUserRepository extends JpaRepository<IdentityUserJpaEntity, Long> {

    Optional<IdentityUserJpaEntity> findByPublicId(UUID publicId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}

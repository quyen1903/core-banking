package com.quinnbank.core.account.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataAccountProductRepository extends JpaRepository<AccountProductJpaEntity, UUID> {

    Optional<AccountProductJpaEntity> findByCode(String code);
}

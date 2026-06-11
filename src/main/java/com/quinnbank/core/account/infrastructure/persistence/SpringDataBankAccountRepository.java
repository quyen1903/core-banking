package com.quinnbank.core.account.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataBankAccountRepository extends JpaRepository<BankAccountJpaEntity, UUID> {

    Optional<BankAccountJpaEntity> findByOpeningIdempotencyKey(String openingIdempotencyKey);
}

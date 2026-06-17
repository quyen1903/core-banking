package com.quinnbank.core.account.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

interface SpringDataBankAccountRepository extends JpaRepository<BankAccountJpaEntity, UUID> {

    Optional<BankAccountJpaEntity> findByOpeningIdempotencyKey(String openingIdempotencyKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select account from BankAccountJpaEntity account where account.id = :accountId")
    Optional<BankAccountJpaEntity> findByIdForUpdate(@Param("accountId") UUID accountId);
}

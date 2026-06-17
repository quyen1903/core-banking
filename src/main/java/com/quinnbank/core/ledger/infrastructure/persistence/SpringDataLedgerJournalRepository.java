package com.quinnbank.core.ledger.infrastructure.persistence;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataLedgerJournalRepository extends JpaRepository<LedgerJournalJpaEntity, UUID> {

    @EntityGraph(attributePaths = "entries")
    Optional<LedgerJournalJpaEntity> findByIdempotencyKey(String idempotencyKey);
}

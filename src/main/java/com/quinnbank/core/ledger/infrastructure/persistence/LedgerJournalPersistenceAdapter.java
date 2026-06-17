package com.quinnbank.core.ledger.infrastructure.persistence;

import com.quinnbank.core.ledger.application.port.out.LedgerJournalRepositoryPort;
import com.quinnbank.core.ledger.domain.model.LedgerJournal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class LedgerJournalPersistenceAdapter implements LedgerJournalRepositoryPort {

    private final SpringDataLedgerJournalRepository repository;
    private final LedgerJournalPersistenceMapper mapper;

    @Override
    public LedgerJournal save(LedgerJournal journal) {
        return mapper.toDomain(repository.save(mapper.toEntity(journal)));
    }

    @Override
    public Optional<LedgerJournal> findByIdempotencyKey(String idempotencyKey) {
        return repository.findByIdempotencyKey(idempotencyKey)
                .map(mapper::toDomain);
    }
}

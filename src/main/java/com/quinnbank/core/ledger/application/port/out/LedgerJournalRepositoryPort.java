package com.quinnbank.core.ledger.application.port.out;

import com.quinnbank.core.ledger.domain.model.LedgerJournal;

import java.util.Optional;

public interface LedgerJournalRepositoryPort {

    LedgerJournal save(LedgerJournal journal);

    Optional<LedgerJournal> findByIdempotencyKey(String idempotencyKey);
}

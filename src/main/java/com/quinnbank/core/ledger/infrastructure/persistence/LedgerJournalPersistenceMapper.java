package com.quinnbank.core.ledger.infrastructure.persistence;

import com.quinnbank.core.common.domain.Money;
import com.quinnbank.core.ledger.domain.model.LedgerEntry;
import com.quinnbank.core.ledger.domain.model.LedgerJournal;
import org.springframework.stereotype.Component;

import java.util.Currency;

@Component
class LedgerJournalPersistenceMapper {

    LedgerJournalJpaEntity toEntity(LedgerJournal journal) {
        return LedgerJournalJpaEntity.create(
                journal.id(),
                journal.sourceCommandId(),
                journal.idempotencyKey(),
                journal.commandFingerprint(),
                journal.actorType(),
                journal.actorId(),
                journal.businessReason(),
                journal.postingDate(),
                journal.valueDate(),
                journal.currency().getCurrencyCode(),
                journal.status(),
                journal.correlationId(),
                journal.createdAt(),
                journal.postedAt(),
                journal.version(),
                journal.entries().stream()
                        .map(this::toEntity)
                        .toList()
        );
    }

    LedgerJournal toDomain(LedgerJournalJpaEntity entity) {
        Currency currency = Currency.getInstance(entity.getCurrency());

        return LedgerJournal.restore(
                entity.getId(),
                entity.getSourceCommandId(),
                entity.getIdempotencyKey(),
                entity.getCommandFingerprint(),
                entity.getActorType(),
                entity.getActorId(),
                entity.getBusinessReason(),
                entity.getPostingDate(),
                entity.getValueDate(),
                currency,
                entity.getStatus(),
                entity.getCorrelationId(),
                entity.getCreatedAt(),
                entity.getPostedAt(),
                entity.getVersion(),
                entity.getEntries().stream()
                        .map(this::toDomain)
                        .toList()
        );
    }

    private LedgerEntryJpaEntity toEntity(LedgerEntry entry) {
        return LedgerEntryJpaEntity.create(
                entry.id(),
                entry.lineNumber(),
                entry.accountId(),
                entry.side(),
                entry.amount().currency().getCurrencyCode(),
                entry.amount().amount(),
                entry.createdAt()
        );
    }

    private LedgerEntry toDomain(LedgerEntryJpaEntity entity) {
        return LedgerEntry.restore(
                entity.getId(),
                entity.getLineNumber(),
                entity.getAccountId(),
                entity.getSide(),
                new Money(entity.getAmount(), Currency.getInstance(entity.getCurrency())),
                entity.getCreatedAt()
        );
    }
}

package com.quinnbank.core.ledger.infrastructure.persistence;

import com.quinnbank.core.ledger.domain.model.LedgerJournalStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ledger_journals")
class LedgerJournalJpaEntity {

    @Id
    private UUID id;

    @Column(name = "source_command_id", nullable = false, length = 120)
    private String sourceCommandId;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 120)
    private String idempotencyKey;

    @Column(name = "command_fingerprint", nullable = false, columnDefinition = "TEXT")
    private String commandFingerprint;

    @Column(name = "actor_type", nullable = false, length = 50)
    private String actorType;

    @Column(name = "actor_id", nullable = false, length = 120)
    private String actorId;

    @Column(name = "business_reason", nullable = false, length = 255)
    private String businessReason;

    @Column(name = "posting_date", nullable = false)
    private LocalDate postingDate;

    @Column(name = "value_date", nullable = false)
    private LocalDate valueDate;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LedgerJournalStatus status;

    @Column(name = "correlation_id", nullable = false, length = 120)
    private String correlationId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "posted_at", nullable = false)
    private LocalDateTime postedAt;

    @Version
    @Column(nullable = false)
    private long version;

    @OrderBy("lineNumber ASC")
    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<LedgerEntryJpaEntity> entries = new ArrayList<>();

    static LedgerJournalJpaEntity create(
            UUID id,
            String sourceCommandId,
            String idempotencyKey,
            String commandFingerprint,
            String actorType,
            String actorId,
            String businessReason,
            LocalDate postingDate,
            LocalDate valueDate,
            String currency,
            LedgerJournalStatus status,
            String correlationId,
            LocalDateTime createdAt,
            LocalDateTime postedAt,
            long version,
            List<LedgerEntryJpaEntity> entries
    ) {
        LedgerJournalJpaEntity entity = new LedgerJournalJpaEntity();
        entity.id = id;
        entity.sourceCommandId = sourceCommandId;
        entity.idempotencyKey = idempotencyKey;
        entity.commandFingerprint = commandFingerprint;
        entity.actorType = actorType;
        entity.actorId = actorId;
        entity.businessReason = businessReason;
        entity.postingDate = postingDate;
        entity.valueDate = valueDate;
        entity.currency = currency;
        entity.status = status;
        entity.correlationId = correlationId;
        entity.createdAt = createdAt;
        entity.postedAt = postedAt;
        entity.version = version;
        entries.forEach(entity::addEntry);
        return entity;
    }

    private void addEntry(LedgerEntryJpaEntity entry) {
        entry.attachTo(this);
        entries.add(entry);
    }
}

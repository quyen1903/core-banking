package com.quinnbank.core.ledger.infrastructure.persistence;

import com.quinnbank.core.ledger.domain.model.LedgerEntrySide;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ledger_entries")
class LedgerEntryJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id", nullable = false)
    private LedgerJournalJpaEntity journal;

    @Column(name = "line_number", nullable = false)
    private int lineNumber;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_side", nullable = false, length = 10)
    private LedgerEntrySide side;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    static LedgerEntryJpaEntity create(
            UUID id,
            int lineNumber,
            UUID accountId,
            LedgerEntrySide side,
            String currency,
            BigDecimal amount,
            LocalDateTime createdAt
    ) {
        LedgerEntryJpaEntity entity = new LedgerEntryJpaEntity();
        entity.id = id;
        entity.lineNumber = lineNumber;
        entity.accountId = accountId;
        entity.side = side;
        entity.currency = currency;
        entity.amount = amount;
        entity.createdAt = createdAt;
        return entity;
    }

    void attachTo(LedgerJournalJpaEntity journal) {
        this.journal = journal;
    }
}

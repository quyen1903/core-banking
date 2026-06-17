package com.quinnbank.core.ledger.domain;

import com.quinnbank.core.common.domain.Money;
import com.quinnbank.core.ledger.domain.exception.LedgerPostingRejectedException;
import com.quinnbank.core.ledger.domain.model.LedgerEntryInput;
import com.quinnbank.core.ledger.domain.model.LedgerEntrySide;
import com.quinnbank.core.ledger.domain.model.LedgerJournal;
import com.quinnbank.core.ledger.domain.model.LedgerJournalStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LedgerJournalTest {

    private static final Currency USD = Currency.getInstance("USD");
    private static final LocalDate POSTING_DATE = LocalDate.of(2026, 6, 12);
    private static final LocalDate VALUE_DATE = LocalDate.of(2026, 6, 12);
    private static final LocalDateTime POSTED_AT = LocalDateTime.of(2026, 6, 12, 2, 0);

    @Test
    void postCreatesBalancedImmutableJournal() {
        UUID debitAccountId = UUID.randomUUID();
        UUID creditAccountId = UUID.randomUUID();

        LedgerJournal journal = LedgerJournal.post(
                "transfer-1",
                "ledger-idem-1",
                "fingerprint-1",
                "service",
                "transfer-service",
                "customer transfer posting",
                POSTING_DATE,
                VALUE_DATE,
                USD,
                "corr-1",
                List.of(
                        entry(debitAccountId, LedgerEntrySide.DEBIT, "100.0000"),
                        entry(creditAccountId, LedgerEntrySide.CREDIT, "100.0000")
                ),
                POSTED_AT
        );

        assertThat(journal.id()).isNotNull();
        assertThat(journal.status()).isEqualTo(LedgerJournalStatus.POSTED);
        assertThat(journal.actorType()).isEqualTo("SERVICE");
        assertThat(journal.currency()).isEqualTo(USD);
        assertThat(journal.entries()).hasSize(2);
        assertThat(journal.entries().get(0).lineNumber()).isEqualTo(1);
        assertThat(journal.entries().get(0).balanceDelta().amount()).isEqualByComparingTo("-100.0000");
        assertThat(journal.entries().get(1).balanceDelta().amount()).isEqualByComparingTo("100.0000");
        assertThat(journal.matchesFingerprint("fingerprint-1")).isTrue();
    }

    @Test
    void postRejectsUnbalancedJournal() {
        assertThatThrownBy(() -> LedgerJournal.post(
                "transfer-1",
                "ledger-idem-1",
                "fingerprint-1",
                "service",
                "transfer-service",
                "customer transfer posting",
                POSTING_DATE,
                VALUE_DATE,
                USD,
                "corr-1",
                List.of(
                        entry(UUID.randomUUID(), LedgerEntrySide.DEBIT, "100.0000"),
                        entry(UUID.randomUUID(), LedgerEntrySide.CREDIT, "99.0000")
                ),
                POSTED_AT
        ))
                .isInstanceOf(LedgerPostingRejectedException.class)
                .hasMessage("ledger debits and credits must balance");
    }

    @Test
    void postRejectsCurrencyMismatch() {
        assertThatThrownBy(() -> LedgerJournal.post(
                "transfer-1",
                "ledger-idem-1",
                "fingerprint-1",
                "service",
                "transfer-service",
                "customer transfer posting",
                POSTING_DATE,
                VALUE_DATE,
                USD,
                "corr-1",
                List.of(
                        entry(UUID.randomUUID(), LedgerEntrySide.DEBIT, "100.0000"),
                        new LedgerEntryInput(
                                UUID.randomUUID(),
                                LedgerEntrySide.CREDIT,
                                new Money(new BigDecimal("100.0000"), Currency.getInstance("EUR"))
                        )
                ),
                POSTED_AT
        ))
                .isInstanceOf(LedgerPostingRejectedException.class)
                .hasMessage("ledger entry currency must match journal currency");
    }

    private static LedgerEntryInput entry(UUID accountId, LedgerEntrySide side, String amount) {
        return new LedgerEntryInput(accountId, side, new Money(new BigDecimal(amount), USD));
    }
}

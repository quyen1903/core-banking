package com.quinnbank.core.ledger.application;

import com.quinnbank.core.account.application.command.ApplyLedgerBalanceProjectionCommand;
import com.quinnbank.core.account.application.port.in.ApplyLedgerBalanceProjectionUseCase;
import com.quinnbank.core.common.domain.Money;
import com.quinnbank.core.ledger.application.command.PostLedgerEntryCommand;
import com.quinnbank.core.ledger.application.command.PostLedgerJournalCommand;
import com.quinnbank.core.ledger.application.exception.LedgerPostingConflictException;
import com.quinnbank.core.ledger.application.port.out.LedgerJournalRepositoryPort;
import com.quinnbank.core.ledger.application.result.LedgerJournalSnapshot;
import com.quinnbank.core.ledger.application.service.PostLedgerJournalService;
import com.quinnbank.core.ledger.domain.model.LedgerEntryInput;
import com.quinnbank.core.ledger.domain.model.LedgerEntrySide;
import com.quinnbank.core.ledger.domain.model.LedgerJournal;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostLedgerJournalUseCaseTest {

    private static final Currency USD = Currency.getInstance("USD");
    private static final LocalDate POSTING_DATE = LocalDate.of(2026, 6, 12);
    private static final LocalDate VALUE_DATE = LocalDate.of(2026, 6, 12);
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-12T02:00:00Z"), ZoneOffset.UTC);

    private final LedgerJournalRepositoryPort ledgerJournalRepositoryPort = mock(LedgerJournalRepositoryPort.class);
    private final ApplyLedgerBalanceProjectionUseCase applyLedgerBalanceProjectionUseCase =
            mock(ApplyLedgerBalanceProjectionUseCase.class);
    private final PostLedgerJournalService useCase = new PostLedgerJournalService(
            ledgerJournalRepositoryPort,
            applyLedgerBalanceProjectionUseCase,
            CLOCK
    );

    @Test
    void postSavesJournalAndAppliesAccountBalanceProjection() {
        UUID debitAccountId = UUID.randomUUID();
        UUID creditAccountId = UUID.randomUUID();
        PostLedgerJournalCommand command = command(debitAccountId, creditAccountId, "100.0000");
        when(ledgerJournalRepositoryPort.findByIdempotencyKey("ledger-idem-1")).thenReturn(Optional.empty());
        when(ledgerJournalRepositoryPort.save(any(LedgerJournal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LedgerJournalSnapshot posted = useCase.post(command);

        assertThat(posted.status()).isEqualTo("POSTED");
        assertThat(posted.entries()).hasSize(2);

        ArgumentCaptor<ApplyLedgerBalanceProjectionCommand> projectionCaptor =
                ArgumentCaptor.forClass(ApplyLedgerBalanceProjectionCommand.class);
        verify(applyLedgerBalanceProjectionUseCase).apply(projectionCaptor.capture());
        ApplyLedgerBalanceProjectionCommand projection = projectionCaptor.getValue();
        assertThat(projection.journalId()).isEqualTo(posted.id());
        assertThat(projection.postedAt()).isEqualTo(LocalDateTime.of(2026, 6, 12, 2, 0));
        assertThat(projection.deltas()).hasSize(2);
        assertThat(projection.deltas())
                .anySatisfy(delta -> {
                    assertThat(delta.accountId()).isEqualTo(debitAccountId);
                    assertThat(delta.delta().amount()).isEqualByComparingTo("-100.0000");
                })
                .anySatisfy(delta -> {
                    assertThat(delta.accountId()).isEqualTo(creditAccountId);
                    assertThat(delta.delta().amount()).isEqualByComparingTo("100.0000");
                });
    }

    @Test
    void postReturnsExistingJournalWhenIdempotencyKeyReplaysSameRequest() {
        UUID debitAccountId = UUID.randomUUID();
        UUID creditAccountId = UUID.randomUUID();
        PostLedgerJournalCommand command = command(debitAccountId, creditAccountId, "100.0000");
        LedgerJournal existing = existingJournal(command, fingerprintFor(command));
        when(ledgerJournalRepositoryPort.findByIdempotencyKey("ledger-idem-1")).thenReturn(Optional.of(existing));

        LedgerJournalSnapshot replay = useCase.post(command);

        assertThat(replay.id()).isEqualTo(existing.id());
        verify(ledgerJournalRepositoryPort, never()).save(any(LedgerJournal.class));
        verify(applyLedgerBalanceProjectionUseCase, never()).apply(any());
    }

    @Test
    void postRejectsSameIdempotencyKeyForDifferentRequest() {
        UUID debitAccountId = UUID.randomUUID();
        UUID creditAccountId = UUID.randomUUID();
        PostLedgerJournalCommand command = command(debitAccountId, creditAccountId, "100.0000");
        LedgerJournal existing = existingJournal(command, "different-fingerprint");
        when(ledgerJournalRepositoryPort.findByIdempotencyKey("ledger-idem-1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> useCase.post(command))
                .isInstanceOf(LedgerPostingConflictException.class)
                .hasMessage("idempotency key was used for a different ledger posting request");

        verify(ledgerJournalRepositoryPort, never()).save(any(LedgerJournal.class));
        verify(applyLedgerBalanceProjectionUseCase, never()).apply(any());
    }

    private static LedgerJournal existingJournal(PostLedgerJournalCommand command, String fingerprint) {
        return LedgerJournal.post(
                command.sourceCommandId(),
                command.idempotencyKey(),
                fingerprint,
                command.actorType(),
                command.actorId(),
                command.businessReason(),
                command.postingDate(),
                command.valueDate(),
                command.currency(),
                command.correlationId(),
                command.entries().stream()
                        .map(entry -> new LedgerEntryInput(entry.accountId(), entry.side(), entry.amount()))
                        .toList(),
                LocalDateTime.of(2026, 6, 12, 2, 0)
        );
    }

    private static PostLedgerJournalCommand command(UUID debitAccountId, UUID creditAccountId, String amount) {
        return new PostLedgerJournalCommand(
                "transfer-1",
                "ledger-idem-1",
                "SERVICE",
                "transfer-service",
                "customer transfer posting",
                POSTING_DATE,
                VALUE_DATE,
                USD,
                "corr-1",
                List.of(
                        entry(debitAccountId, LedgerEntrySide.DEBIT, amount),
                        entry(creditAccountId, LedgerEntrySide.CREDIT, amount)
                )
        );
    }

    private static PostLedgerEntryCommand entry(UUID accountId, LedgerEntrySide side, String amount) {
        return new PostLedgerEntryCommand(accountId, side, new Money(new BigDecimal(amount), USD));
    }

    private static String fingerprintFor(PostLedgerJournalCommand command) {
        StringBuilder fingerprint = new StringBuilder()
                .append(command.sourceCommandId()).append('|')
                .append(command.actorType()).append('|')
                .append(command.actorId()).append('|')
                .append(command.businessReason()).append('|')
                .append(command.postingDate()).append('|')
                .append(command.valueDate()).append('|')
                .append(command.currency().getCurrencyCode()).append('|')
                .append(command.correlationId());

        for (PostLedgerEntryCommand entry : command.entries()) {
            fingerprint.append('|')
                    .append(entry.accountId()).append(':')
                    .append(entry.side()).append(':')
                    .append(entry.amount().currency().getCurrencyCode()).append(':')
                    .append(entry.amount().amount().toPlainString());
        }
        return fingerprint.toString();
    }
}

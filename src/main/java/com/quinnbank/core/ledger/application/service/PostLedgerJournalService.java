package com.quinnbank.core.ledger.application.service;

import com.quinnbank.core.account.application.command.AccountBalanceDelta;
import com.quinnbank.core.account.application.command.ApplyLedgerBalanceProjectionCommand;
import com.quinnbank.core.account.application.port.in.ApplyLedgerBalanceProjectionUseCase;
import com.quinnbank.core.common.domain.Money;
import com.quinnbank.core.ledger.application.command.PostLedgerEntryCommand;
import com.quinnbank.core.ledger.application.command.PostLedgerJournalCommand;
import com.quinnbank.core.ledger.application.exception.LedgerPostingConflictException;
import com.quinnbank.core.ledger.application.port.in.PostLedgerJournalUseCase;
import com.quinnbank.core.ledger.application.port.out.LedgerJournalRepositoryPort;
import com.quinnbank.core.ledger.application.result.LedgerJournalSnapshot;
import com.quinnbank.core.ledger.domain.exception.LedgerPostingRejectedException;
import com.quinnbank.core.ledger.domain.model.LedgerEntry;
import com.quinnbank.core.ledger.domain.model.LedgerEntryInput;
import com.quinnbank.core.ledger.domain.model.LedgerJournal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostLedgerJournalService implements PostLedgerJournalUseCase {

    private final LedgerJournalRepositoryPort ledgerJournalRepositoryPort;
    private final ApplyLedgerBalanceProjectionUseCase applyLedgerBalanceProjectionUseCase;
    private final Clock clock;

    @Override
    @Transactional
    public LedgerJournalSnapshot post(PostLedgerJournalCommand command) {
        validate(command);

        String idempotencyKey = command.idempotencyKey().trim();
        String commandFingerprint = commandFingerprint(command);

        return ledgerJournalRepositoryPort.findByIdempotencyKey(idempotencyKey)
                .map(existing -> replayOrReject(existing, commandFingerprint))
                .orElseGet(() -> postNewJournal(command, idempotencyKey, commandFingerprint));
    }

    private LedgerJournalSnapshot postNewJournal(
            PostLedgerJournalCommand command,
            String idempotencyKey,
            String commandFingerprint
    ) {
        LocalDateTime postedAt = LocalDateTime.now(clock);
        LedgerJournal journal = LedgerJournal.post(
                command.sourceCommandId(),
                idempotencyKey,
                commandFingerprint,
                command.actorType(),
                command.actorId(),
                command.businessReason(),
                command.postingDate(),
                command.valueDate(),
                command.currency(),
                command.correlationId(),
                entryInputs(command.entries()),
                postedAt
        );

        LedgerJournal savedJournal = ledgerJournalRepositoryPort.save(journal);
        applyLedgerBalanceProjectionUseCase.apply(new ApplyLedgerBalanceProjectionCommand(
                savedJournal.id(),
                balanceDeltas(savedJournal),
                savedJournal.postedAt()
        ));
        return LedgerJournalSnapshot.from(savedJournal);
    }

    private LedgerJournalSnapshot replayOrReject(LedgerJournal existing, String commandFingerprint) {
        if (!existing.matchesFingerprint(commandFingerprint)) {
            throw new LedgerPostingConflictException("idempotency key was used for a different ledger posting request");
        }

        return LedgerJournalSnapshot.from(existing);
    }

    private static List<LedgerEntryInput> entryInputs(List<PostLedgerEntryCommand> entries) {
        return entries.stream()
                .map(entry -> new LedgerEntryInput(entry.accountId(), entry.side(), entry.amount()))
                .toList();
    }

    private static List<AccountBalanceDelta> balanceDeltas(LedgerJournal journal) {
        Map<UUID, Money> deltas = new LinkedHashMap<>();
        for (LedgerEntry entry : journal.entries()) {
            deltas.merge(entry.accountId(), entry.balanceDelta(), Money::add);
        }

        return deltas.entrySet().stream()
                .filter(entry -> !entry.getValue().isZero())
                .map(entry -> new AccountBalanceDelta(entry.getKey(), entry.getValue()))
                .toList();
    }

    private static void validate(PostLedgerJournalCommand command) {
        if (command == null) {
            throw new LedgerPostingRejectedException("ledger posting command is required");
        }
        if (command.sourceCommandId() == null || command.sourceCommandId().isBlank()) {
            throw new LedgerPostingRejectedException("source command id is required");
        }
        if (command.idempotencyKey() == null || command.idempotencyKey().isBlank()) {
            throw new LedgerPostingRejectedException("idempotency key is required");
        }
        if (command.idempotencyKey().length() > 120) {
            throw new LedgerPostingRejectedException("idempotency key is too long");
        }
        if (command.actorType() == null || command.actorType().isBlank()) {
            throw new LedgerPostingRejectedException("actor type is required");
        }
        if (command.actorId() == null || command.actorId().isBlank()) {
            throw new LedgerPostingRejectedException("actor id is required");
        }
        if (command.businessReason() == null || command.businessReason().isBlank()) {
            throw new LedgerPostingRejectedException("business reason is required");
        }
        if (command.postingDate() == null) {
            throw new LedgerPostingRejectedException("posting date is required");
        }
        if (command.valueDate() == null) {
            throw new LedgerPostingRejectedException("value date is required");
        }
        if (command.currency() == null) {
            throw new LedgerPostingRejectedException("currency is required");
        }
        if (command.correlationId() == null || command.correlationId().isBlank()) {
            throw new LedgerPostingRejectedException("correlation id is required");
        }
        if (command.entries() == null || command.entries().isEmpty()) {
            throw new LedgerPostingRejectedException("ledger entries are required");
        }
        for (PostLedgerEntryCommand entry : command.entries()) {
            if (entry == null) {
                throw new LedgerPostingRejectedException("ledger entry is required");
            }
            if (entry.accountId() == null) {
                throw new LedgerPostingRejectedException("ledger entry account id is required");
            }
            if (entry.side() == null) {
                throw new LedgerPostingRejectedException("ledger entry side is required");
            }
            if (entry.amount() == null) {
                throw new LedgerPostingRejectedException("ledger entry amount must be positive");
            }
        }
    }

    private static String commandFingerprint(PostLedgerJournalCommand command) {
        StringBuilder fingerprint = new StringBuilder()
                .append(command.sourceCommandId().trim()).append('|')
                .append(command.actorType().trim().toUpperCase()).append('|')
                .append(command.actorId().trim()).append('|')
                .append(command.businessReason().trim()).append('|')
                .append(command.postingDate()).append('|')
                .append(command.valueDate()).append('|')
                .append(command.currency().getCurrencyCode()).append('|')
                .append(command.correlationId().trim());

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

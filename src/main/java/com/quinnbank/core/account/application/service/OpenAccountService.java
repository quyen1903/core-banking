package com.quinnbank.core.account.application.service;

import com.quinnbank.core.account.application.command.OpenAccountCommand;
import com.quinnbank.core.account.application.exception.AccountNotFoundException;
import com.quinnbank.core.account.application.exception.AccountOpeningConflictException;
import com.quinnbank.core.account.application.exception.AccountProductNotFoundException;
import com.quinnbank.core.account.application.port.in.OpenAccountUseCase;
import com.quinnbank.core.account.application.port.out.AccountNumberGeneratorPort;
import com.quinnbank.core.account.application.port.out.AccountProductLookupPort;
import com.quinnbank.core.account.application.port.out.AccountRepositoryPort;
import com.quinnbank.core.account.application.port.out.CustomerLookupPort;
import com.quinnbank.core.account.application.port.out.IdempotencyPort;
import com.quinnbank.core.account.application.result.AccountOpeningIdempotencyResult;
import com.quinnbank.core.account.application.result.AccountSnapshot;
import com.quinnbank.core.account.domain.exception.AccountOpeningRejectedException;
import com.quinnbank.core.account.domain.model.AccountProduct;
import com.quinnbank.core.account.domain.model.BankAccount;
import com.quinnbank.core.account.domain.policy.AccountOpeningPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OpenAccountService implements OpenAccountUseCase {

    private final CustomerLookupPort customerLookupPort;
    private final AccountProductLookupPort accountProductLookupPort;
    private final AccountRepositoryPort accountRepositoryPort;
    private final IdempotencyPort idempotencyPort;
    private final AccountNumberGeneratorPort accountNumberGeneratorPort;
    private final AccountOpeningPolicy accountOpeningPolicy;
    private final Clock clock;

    @Override
    @Transactional
    public AccountSnapshot open(OpenAccountCommand command) {
        validate(command);

        String productCode = command.productCode().trim().toUpperCase();
        String idempotencyKey = command.idempotencyKey().trim();
        String requestFingerprint = requestFingerprint(command.customerId(), productCode);

        return idempotencyPort.findAccountOpening(idempotencyKey)
                .map(existing -> replayOrReject(existing, requestFingerprint))
                .orElseGet(() -> openNewAccount(command, productCode, idempotencyKey, requestFingerprint));
    }

    private AccountSnapshot openNewAccount(
            OpenAccountCommand command,
            String productCode,
            String idempotencyKey,
            String requestFingerprint
    ) {
        customerLookupPort.requireActiveCustomer(command.customerId());

        AccountProduct product = accountProductLookupPort.findActiveOrInactiveByCode(productCode)
                .orElseThrow(() -> new AccountProductNotFoundException("account product not found"));

        accountOpeningPolicy.verify(product);

        BankAccount account = BankAccount.open(
                product,
                command.customerId(),
                accountNumberGeneratorPort.nextAccountNumber(),
                idempotencyKey,
                requestFingerprint,
                LocalDateTime.now(clock)
        );

        return AccountSnapshot.from(accountRepositoryPort.save(account));
    }

    private AccountSnapshot replayOrReject(
            AccountOpeningIdempotencyResult existing,
            String requestFingerprint
    ) {
        if (!existing.matches(requestFingerprint)) {
            throw new AccountOpeningConflictException("idempotency key was used for a different account opening request");
        }

        return accountRepositoryPort.findById(existing.accountId())
                .map(AccountSnapshot::from)
                .orElseThrow(() -> AccountNotFoundException.byId(existing.accountId()));
    }

    private static void validate(OpenAccountCommand command) {
        if (command == null) {
            throw new AccountOpeningRejectedException("open account command is required");
        }
        if (command.customerId() == null) {
            throw new AccountOpeningRejectedException("customer id is required");
        }
        if (command.productCode() == null || command.productCode().isBlank()) {
            throw new AccountOpeningRejectedException("product code is required");
        }
        if (command.idempotencyKey() == null || command.idempotencyKey().isBlank()) {
            throw new AccountOpeningRejectedException("idempotency key is required");
        }
        if (command.idempotencyKey().length() > 120) {
            throw new AccountOpeningRejectedException("idempotency key is too long");
        }
    }

    private static String requestFingerprint(Object customerId, String productCode) {
        return customerId + "|" + productCode;
    }
}

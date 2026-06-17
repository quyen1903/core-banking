package com.quinnbank.core.account.domain.model;

import com.quinnbank.core.account.domain.event.AccountOpenedEvent;
import com.quinnbank.core.account.domain.exception.AccountBalanceProjectionRejectedException;
import com.quinnbank.core.account.domain.exception.AccountOpeningRejectedException;
import com.quinnbank.core.common.domain.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class BankAccount {

    private final UUID id;
    private final AccountNumber accountNumber;
    private final UUID customerId;
    private final UUID productId;
    private final Money availableBalance;
    private final Money currentBalance;
    private final BankAccountStatus status;
    private final LocalDateTime openedAt;
    private final LocalDateTime closedAt;
    private final String openingIdempotencyKey;
    private final String openingRequestFingerprint;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final long version;
    private final List<Object> domainEvents = new ArrayList<>();

    private BankAccount(
            UUID id,
            AccountNumber accountNumber,
            UUID customerId,
            UUID productId,
            Money availableBalance,
            Money currentBalance,
            BankAccountStatus status,
            LocalDateTime openedAt,
            LocalDateTime closedAt,
            String openingIdempotencyKey,
            String openingRequestFingerprint,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long version
    ) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.productId = productId;
        this.availableBalance = availableBalance;
        this.currentBalance = currentBalance;
        this.status = status;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.openingIdempotencyKey = openingIdempotencyKey;
        this.openingRequestFingerprint = openingRequestFingerprint;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static BankAccount open(
            AccountProduct product,
            UUID customerId,
            AccountNumber accountNumber,
            String idempotencyKey,
            String requestFingerprint,
            LocalDateTime openedAt
    ) {
        if (product == null) {
            throw new AccountOpeningRejectedException("account product is required");
        }
        if (customerId == null) {
            throw new AccountOpeningRejectedException("customer id is required");
        }
        if (accountNumber == null) {
            throw new AccountOpeningRejectedException("account number is required");
        }
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new AccountOpeningRejectedException("idempotency key is required");
        }
        if (requestFingerprint == null || requestFingerprint.isBlank()) {
            throw new AccountOpeningRejectedException("request fingerprint is required");
        }
        if (openedAt == null) {
            throw new AccountOpeningRejectedException("opening time is required");
        }

        BankAccount account = new BankAccount(
                UUID.randomUUID(),
                accountNumber,
                customerId,
                product.id(),
                Money.zero(product.currency()),
                Money.zero(product.currency()),
                BankAccountStatus.OPEN,
                openedAt,
                null,
                idempotencyKey.trim(),
                requestFingerprint.trim(),
                openedAt,
                openedAt,
                0
        );
        account.domainEvents.add(new AccountOpenedEvent(
                account.id,
                account.accountNumber,
                account.customerId,
                account.productId,
                account.openingIdempotencyKey,
                openedAt
        ));

        return account;
    }

    public static BankAccount restore(
            UUID id,
            AccountNumber accountNumber,
            UUID customerId,
            UUID productId,
            Money availableBalance,
            Money currentBalance,
            BankAccountStatus status,
            LocalDateTime openedAt,
            LocalDateTime closedAt,
            String openingIdempotencyKey,
            String openingRequestFingerprint,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long version
    ) {
        if (id == null) {
            throw new IllegalArgumentException("account id is required");
        }
        if (accountNumber == null) {
            throw new IllegalArgumentException("account number is required");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("customer id is required");
        }
        if (productId == null) {
            throw new IllegalArgumentException("product id is required");
        }
        if (availableBalance == null || currentBalance == null) {
            throw new IllegalArgumentException("account balances are required");
        }
        if (!availableBalance.currency().equals(currentBalance.currency())) {
            throw new IllegalArgumentException("account balance currencies must match");
        }
        if (status == null) {
            throw new IllegalArgumentException("account status is required");
        }
        if (openedAt == null) {
            throw new IllegalArgumentException("account opening time is required");
        }
        if (status == BankAccountStatus.CLOSED && closedAt == null) {
            throw new IllegalArgumentException("closed account requires closure time");
        }
        if (openingIdempotencyKey == null || openingIdempotencyKey.isBlank()) {
            throw new IllegalArgumentException("opening idempotency key is required");
        }
        if (openingRequestFingerprint == null || openingRequestFingerprint.isBlank()) {
            throw new IllegalArgumentException("opening request fingerprint is required");
        }
        if (createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("account timestamps are required");
        }

        return new BankAccount(
                id,
                accountNumber,
                customerId,
                productId,
                availableBalance,
                currentBalance,
                status,
                openedAt,
                closedAt,
                openingIdempotencyKey.trim(),
                openingRequestFingerprint.trim(),
                createdAt,
                updatedAt,
                version
        );
    }

    public BankAccount applyLedgerProjection(Money delta, LocalDateTime appliedAt) {
        if (delta == null) {
            throw new AccountBalanceProjectionRejectedException("ledger balance delta is required");
        }
        if (appliedAt == null) {
            throw new AccountBalanceProjectionRejectedException("ledger projection time is required");
        }
        if (status != BankAccountStatus.OPEN) {
            throw new AccountBalanceProjectionRejectedException("account is not open for ledger posting");
        }
        if (!availableBalance.currency().equals(delta.currency())) {
            throw new AccountBalanceProjectionRejectedException("ledger balance delta currency must match account currency");
        }

        Money projectedAvailableBalance = availableBalance.add(delta);
        Money projectedCurrentBalance = currentBalance.add(delta);
        if (projectedAvailableBalance.amount().compareTo(BigDecimal.ZERO) < 0
                || projectedCurrentBalance.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountBalanceProjectionRejectedException("ledger posting would make account balance negative");
        }

        return new BankAccount(
                id,
                accountNumber,
                customerId,
                productId,
                projectedAvailableBalance,
                projectedCurrentBalance,
                status,
                openedAt,
                closedAt,
                openingIdempotencyKey,
                openingRequestFingerprint,
                createdAt,
                appliedAt,
                version
        );
    }

    public List<Object> pullDomainEvents() {
        List<Object> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public UUID id() {
        return id;
    }

    public AccountNumber accountNumber() {
        return accountNumber;
    }

    public UUID customerId() {
        return customerId;
    }

    public UUID productId() {
        return productId;
    }

    public Money availableBalance() {
        return availableBalance;
    }

    public Money currentBalance() {
        return currentBalance;
    }

    public BankAccountStatus status() {
        return status;
    }

    public LocalDateTime openedAt() {
        return openedAt;
    }

    public LocalDateTime closedAt() {
        return closedAt;
    }

    public String openingIdempotencyKey() {
        return openingIdempotencyKey;
    }

    public String openingRequestFingerprint() {
        return openingRequestFingerprint;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }

    public long version() {
        return version;
    }
}

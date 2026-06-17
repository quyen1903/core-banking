package com.quinnbank.core.account.infrastructure.persistence;

import com.quinnbank.core.account.domain.model.BankAccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "bank_accounts")
class BankAccountJpaEntity {

    @Id
    private UUID id;

    @Column(name = "account_number", nullable = false, unique = true, length = 50)
    private String accountNumber;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "available_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal availableBalance;

    @Column(name = "current_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal currentBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BankAccountStatus status;

    @Column(name = "opening_idempotency_key", nullable = false, unique = true, length = 120)
    private String openingIdempotencyKey;

    @Column(name = "opening_request_fingerprint", nullable = false, length = 255)
    private String openingRequestFingerprint;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    static BankAccountJpaEntity create(
            UUID id,
            String accountNumber,
            UUID customerId,
            UUID productId,
            String currency,
            BigDecimal availableBalance,
            BigDecimal currentBalance,
            BankAccountStatus status,
            String openingIdempotencyKey,
            String openingRequestFingerprint,
            LocalDateTime openedAt,
            LocalDateTime closedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long version
    ) {
        BankAccountJpaEntity entity = new BankAccountJpaEntity();
        entity.id = id;
        entity.accountNumber = accountNumber;
        entity.customerId = customerId;
        entity.productId = productId;
        entity.currency = currency;
        entity.availableBalance = availableBalance;
        entity.currentBalance = currentBalance;
        entity.status = status;
        entity.openingIdempotencyKey = openingIdempotencyKey;
        entity.openingRequestFingerprint = openingRequestFingerprint;
        entity.openedAt = openedAt;
        entity.closedAt = closedAt;
        entity.createdAt = createdAt;
        entity.updatedAt = updatedAt;
        entity.version = version;
        return entity;
    }
}

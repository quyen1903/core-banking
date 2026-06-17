package com.quinnbank.core.common.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import org.springframework.data.domain.Auditable;

import java.time.OffsetDateTime;
import java.util.Optional;

@MappedSuperclass
public abstract class AbstractAuditableWithUTCDateTimeCustom<T> implements Auditable<Long, T, OffsetDateTime> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="created_by", updatable = false, nullable = false)
    @Setter
    private Long createdBy;

    @Column(name = "created_on_utc", updatable = false, nullable = false)
    @Setter
    private OffsetDateTime createdDate;

    @Column(name = "last_modified_by", nullable = false)
    @Setter
    private Long lastModifiedBy;

    @Column(name = "last_modified_on_utc", nullable = false)
    @Setter
    private OffsetDateTime lastModifiedDate;

    @Override
    @NotNull
    public Optional<Long> getCreatedBy() {
        return Optional.ofNullable(this.createdBy);
    }

    @Override
    @NotNull
    public Optional<OffsetDateTime> getCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    @Override
    @NotNull
    public Optional<Long> getLastModifiedBy() {
        return Optional.ofNullable(this.lastModifiedBy);
    }

    @Override
    @NotNull
    public Optional<OffsetDateTime> getLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate);
    }
}


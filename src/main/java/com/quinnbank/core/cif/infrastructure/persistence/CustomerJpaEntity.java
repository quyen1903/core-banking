package com.quinnbank.core.cif.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class CustomerJpaEntity {
    @Id
    UUID id;

    @Column(name = "customer_number", nullable = false, unique = true, length = 50)
    String customerNumber;

    @Column(name = "full_name", nullable = false, length = 511)
    String fullName;

    @Column(name = "first_name", length = 255)
    String firstName;

    @Column(name = "last_name", length = 255)
    String lastName;

    @Column(name = "office_id", length = 50)
    String officeId;

    @Column(name = "external_id", length = 50)
    String externalId;

    @Column(unique = true, length = 255)
    String email;

    @Column(length = 50)
    String phone;

    @Column(nullable = false, length = 50)
    String status;

    @Column(name = "kyc_status", nullable = false, length = 50)
    String kycStatus;

    @Column(name = "risk_rating", nullable = false, length = 50)
    String riskRating;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    Long version;

    protected CustomerJpaEntity() {
    }
}

package com.quinnbank.core.cif.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "customers",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_customers_customer_number", columnNames = "customer_number"),
        @UniqueConstraint(name = "uq_customers_email", columnNames = "email")
    }
)
public class CustomerJpaEntity {
    @Id
    UUID id;

    @Column(name = "customer_number", nullable = false, length = 50)
    String customerNumber;

    @Column(name = "first_name", length = 255)
    String firstName;

    @Column(name = "last_name", length = 255)
    String lastName;

    @Column(name = "office_id", length = 50)
    String officeId;

    @Column(name = "external_id", length = 50)
    String externalId;

    @Column(length = 255)
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

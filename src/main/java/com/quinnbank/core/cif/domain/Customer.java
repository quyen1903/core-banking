package com.quinnbank.core.cif.domain;

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

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    private UUID id;

    @Column(name = "customer_number", nullable = false, unique = true, length = 50)
    private String customerNumber;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(unique = true, length = 255)
    private String email;

    @Column(length = 50)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CustomerStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", nullable = false, length = 50)
    private KycStatus kycStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_rating", nullable = false, length = 50)
    private RiskRating riskRating;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    public static Customer register(
            String customerNumber,
            String fullName,
            String email,
            String phone,
            LocalDateTime registeredAt
    ) {
        if (customerNumber == null || customerNumber.isBlank()) {
            throw new IllegalArgumentException("customer number is required");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("full name is required");
        }
        if (registeredAt == null) {
            throw new IllegalArgumentException("registration time is required");
        }

        Customer customer = new Customer();
        customer.id = UUID.randomUUID();
        customer.customerNumber = customerNumber.trim();
        customer.fullName = fullName.trim();
        customer.email = normalizeEmail(email);
        customer.phone = normalizePhone(phone);
        customer.status = CustomerStatus.ACTIVE;
        customer.kycStatus = KycStatus.NOT_STARTED;
        customer.riskRating = RiskRating.LOW;
        customer.createdAt = registeredAt;
        customer.updatedAt = registeredAt;
        customer.version = 0;

        return customer;
    }

    public boolean isActive() {
        return status == CustomerStatus.ACTIVE;
    }

    public static String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return email.trim().toLowerCase();
    }

    private static String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        return phone.trim();
    }
}

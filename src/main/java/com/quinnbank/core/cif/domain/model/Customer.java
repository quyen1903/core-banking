package com.quinnbank.core.cif.domain.model;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import com.quinnbank.core.cif.domain.enums.CustomerStatus;
import com.quinnbank.core.cif.domain.enums.KycStatus;
import com.quinnbank.core.cif.domain.enums.RiskRating;
import com.quinnbank.core.cif.domain.exception.CustomerRegistrationRejectedException;

/** Customer registration aggregate. Persistence and transport concerns stay in adapters. */
public final class Customer {
    private final UUID id;
    private final String customerNumber;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final String officeId;
    private final String externalId;
    private final LocalDateTime createdAt;

    private Customer(
        String customerNumber,
        String firstName,
        String lastName,
        String email,
        String phone,
        String officeId,
        String externalId,
        LocalDateTime registeredAt
    ) {
        this.id = UUID.randomUUID();
        this.customerNumber = required(customerNumber, "customer number", 50);
        this.firstName = required(firstName, "first name", 255);
        this.lastName = required(lastName, "last name", 255);
        this.email = optional(normalizeEmail(email), "email", 255);
        this.phone = optional(phone, "phone", 50);
        this.officeId = required(officeId, "office id", 50);
        this.externalId = optional(externalId, "external id", 50);
        if (registeredAt == null) {
            throw new CustomerRegistrationRejectedException("registration time is required");
        }
        this.createdAt = registeredAt;
    }

    public static Customer register(
        String customerNumber,
        String firstName,
        String lastName,
        String email,
        String phone,
        String officeId,
        String externalId,
        LocalDateTime registeredAt
    ) {
        return new Customer(
            customerNumber,
            firstName,
            lastName,
            email,
            phone,
            officeId,
            externalId,
            registeredAt
        );
    }

    public static String normalizeEmail(String email) {
        return email == null || email.isBlank() ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private static String required(String value, String field, int maxLength) {
        String normalized = optional(value, field, maxLength);
        if (normalized == null) {
            throw new CustomerRegistrationRejectedException(field + " is required");
        }
        return normalized;
    }

    private static String optional(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new CustomerRegistrationRejectedException(field + " is too long");
        }
        return normalized;
    }

    public UUID getId() {
        return id;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getOfficeId() {
        return officeId;
    }

    public String getExternalId() {
        return externalId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public CustomerStatus getStatus() {
        return CustomerStatus.PENDING;
    }

    public KycStatus getKycStatus() {
        return KycStatus.NOT_STARTED;
    }

    public RiskRating getRiskRating() {
        return RiskRating.LOW;
    }

    public LocalDateTime getUpdatedAt() {
        return createdAt;
    }

    public long getVersion() {
        return 0;
    }
}

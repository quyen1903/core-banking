package com.quinnbank.core.cif.domain;

import com.quinnbank.core.cif.domain.enums.CustomerStatus;
import com.quinnbank.core.cif.domain.enums.KycStatus;
import com.quinnbank.core.cif.domain.enums.RiskRating;
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

    //Unique customer profile number (CIF), used for customer lookup.
    @Column(name = "customer_number", nullable = false, unique = true, length = 50)
    private String customerNumber;

    //external ID used to link the customer with external systems, such as a CRM or KYC provider
    @Column(name = "external_id", nullable = false, length = 50)
    private String externalId;

    @Column(name = "first_name", nullable = false, length = 255)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 255)
    private String lastName;

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
            String firstName,
            String lastName,
            String email,
            String phone,
            LocalDateTime registeredAt
    ) {
        if (customerNumber == null || customerNumber.isBlank()) {
            throw new IllegalArgumentException("customer number is required");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("first name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("last name is required");
        }
        if (registeredAt == null) {
            throw new IllegalArgumentException("registration time is required");
        }

        Customer customer = new Customer();
        customer.id = UUID.randomUUID();
        customer.customerNumber = customerNumber.trim();
        customer.externalId = UUID.randomUUID().toString();
        customer.customerNumber = customerNumber.trim();
        customer.firstName = firstName.trim();
        customer.lastName = lastName.trim();
        customer.email = normalizeEmail(email);
        customer.phone = normalizePhone(phone);
        customer.status = CustomerStatus.PENDING;
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

package com.quinnbank.core.customer.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    private UUID id;

    @Column(name = "customer_number", nullable = false, unique = true)
    private String customerNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(unique = true)
    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static Customer create(String fullName, String email, String phone) {
        LocalDateTime now = LocalDateTime.now();

        Customer customer = new Customer();
        customer.id = UUID.randomUUID();
        customer.customerNumber = "CIF" + System.currentTimeMillis();
        customer.fullName = fullName;
        customer.email = normalizeEmail(email);
        customer.phone = phone;
        customer.status = CustomerStatus.ACTIVE;
        customer.createdAt = now;
        customer.updatedAt = now;

        return customer;
    }

    private static String normalizeEmail(String email){
        if (email == null || email.isBlank()){
            return null;
        }
        return email.trim().toLowerCase();
    }
}

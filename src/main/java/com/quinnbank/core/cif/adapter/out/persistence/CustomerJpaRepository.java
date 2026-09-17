package com.quinnbank.core.cif.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quinnbank.core.cif.domain.Customer;

@Repository
public interface CustomerJpaRepository extends JpaRepository<Customer, UUID> {
    boolean existsByEmail(String email);
    Optional<Customer> findByCustomerNumber(String customerNumber);
    Optional<Customer> findById(UUID customerId);
}

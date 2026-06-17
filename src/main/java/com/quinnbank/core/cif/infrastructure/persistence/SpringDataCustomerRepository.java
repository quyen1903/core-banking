package com.quinnbank.core.cif.infrastructure.persistence;

import com.quinnbank.core.cif.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataCustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByEmail(String email);

    Optional<Customer> findByCustomerNumber(String customerNumber);
}

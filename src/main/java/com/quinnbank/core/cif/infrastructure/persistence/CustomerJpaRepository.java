package com.quinnbank.core.cif.infrastructure.persistence;

import com.quinnbank.core.cif.application.result.GetCustomerByIdResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, UUID> {
    boolean existsByEmail(String email);

    @Query("""
            select new com.quinnbank.core.cif.application.result.GetCustomerByIdResult(
                customer.id, customer.firstName, customer.lastName, customer.email,
                customer.phone, customer.fullName)
            from CustomerJpaEntity customer
            where customer.id = :customerId
            """)
    Optional<GetCustomerByIdResult> findDetailsById(@Param("customerId") UUID customerId);
}

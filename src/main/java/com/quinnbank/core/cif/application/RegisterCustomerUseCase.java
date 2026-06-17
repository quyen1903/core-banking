package com.quinnbank.core.cif.application;

import com.quinnbank.core.cif.domain.Customer;
import com.quinnbank.core.cif.domain.CustomerNumberGenerator;
import com.quinnbank.core.cif.application.port.out.CustomerRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerNumberGenerator customerNumberGenerator;
    private final Clock clock;

    @Transactional
    public CustomerSnapshot register(RegisterCustomerCommand command) {
        String normalizedEmail = Customer.normalizeEmail(command.email());

        if (normalizedEmail != null && customerRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateCustomerEmailException("email already exists");
        }

        Customer customer = Customer.register(
                customerNumberGenerator.nextCustomerNumber(),
                command.fullName(),
                normalizedEmail,
                command.phone(),
                LocalDateTime.now(clock)
        );

        return CustomerSnapshot.from(customerRepository.save(customer));
    }
}

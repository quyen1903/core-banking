package com.quinnbank.core.cif.application;

import com.quinnbank.core.cif.application.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.port.out.CustomerNumberGeneratorPort;
import com.quinnbank.core.cif.application.port.out.CustomerWritePort;
import com.quinnbank.core.cif.application.result.RegisterCustomerResult;
import com.quinnbank.core.cif.application.service.CustomerCommandService;
import com.quinnbank.core.cif.domain.exception.CustomerRegistrationRejectedException;
import com.quinnbank.core.cif.domain.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CustomerCommandServiceTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-09-20T09:30:00Z"), ZoneOffset.UTC);

    private CustomerWritePort customers;
    private CustomerNumberGeneratorPort customerNumbers;
    private CustomerCommandService service;

    @BeforeEach
    void setUp() {
        customers = mock(CustomerWritePort.class);
        customerNumbers = mock(CustomerNumberGeneratorPort.class);
        service = new CustomerCommandService(customers, customerNumbers, FIXED_CLOCK);
    }

    @Test
    void registersUsingNormalizedEmailGeneratedNumberAndInjectedClock() {
        when(customerNumbers.nextCustomerNumber()).thenReturn("CIF-TEST-001");

        RegisterCustomerResult result = service.registerCustomer(command(" SYNTHETIC@EXAMPLE.INVALID "));

        ArgumentCaptor<Customer> persisted = ArgumentCaptor.forClass(Customer.class);
        verify(customers).existsByEmail("synthetic@example.invalid");
        verify(customers).save(persisted.capture());
        Customer customer = persisted.getValue();
        assertThat(customer.getCustomerNumber()).isEqualTo("CIF-TEST-001");
        assertThat(customer.getEmail()).isEqualTo("synthetic@example.invalid");
        assertThat(customer.getOfficeId()).isEqualTo("local-only-office");
        assertThat(customer.getExternalId()).isEqualTo("local-only-crm-reference");
        assertThat(customer.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 9, 20, 9, 30));
        assertThat(customer.getUpdatedAt()).isEqualTo(customer.getCreatedAt());
        assertThat(result.customerId()).isEqualTo(customer.getId());
        assertThat(result.status()).isEqualTo("PENDING");
    }

    @Test
    void rejectsDuplicateNormalizedEmailBeforeAllocatingNumberOrSaving() {
        when(customers.existsByEmail("synthetic@example.invalid")).thenReturn(true);

        assertThatThrownBy(() -> service.registerCustomer(command(" SYNTHETIC@EXAMPLE.INVALID ")))
                .isInstanceOf(DuplicateCustomerEmailException.class)
                .hasMessageNotContaining("synthetic@example.invalid")
                .hasMessageNotContaining("SYNTHETIC@EXAMPLE.INVALID");

        verify(customers).existsByEmail("synthetic@example.invalid");
        verify(customers, never()).save(any(Customer.class));
        verifyNoInteractions(customerNumbers);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void doesNotTreatMissingEmailAsDuplicateKey(String absentEmail) {
        when(customerNumbers.nextCustomerNumber()).thenReturn("CIF-TEST-001");

        service.registerCustomer(command(absentEmail));

        ArgumentCaptor<Customer> persisted = ArgumentCaptor.forClass(Customer.class);
        verify(customers, never()).existsByEmail(any());
        verify(customers).save(persisted.capture());
        assertThat(persisted.getValue().getEmail()).isNull();
    }

    @Test
    void neverPersistsAnInvalidRegistration() {
        when(customerNumbers.nextCustomerNumber()).thenReturn("CIF-TEST-001");
        RegisterCustomerCommand invalid = new RegisterCustomerCommand(" ", "Customer", null,
                null, "local-only-office", null);

        assertThatThrownBy(() -> service.registerCustomer(invalid))
                .isInstanceOf(CustomerRegistrationRejectedException.class);

        verify(customers, never()).save(any(Customer.class));
        verify(customers, never()).existsByEmail(anyString());
    }

    @Test
    void rejectsNullCommandBeforeCallingOutboundPorts() {
        assertThatThrownBy(() -> service.registerCustomer(null))
                .isInstanceOf(CustomerRegistrationRejectedException.class);

        verifyNoInteractions(customers, customerNumbers);
    }

    @Test
    void propagatesPersistenceFailureInsteadOfReportingSuccessfulRegistration() {
        when(customerNumbers.nextCustomerNumber()).thenReturn("CIF-TEST-001");
        IllegalStateException failure = new IllegalStateException("synthetic persistence failure");
        doThrow(failure).when(customers).save(any(Customer.class));

        assertThatThrownBy(() -> service.registerCustomer(command(null))).isSameAs(failure);
    }

    private static RegisterCustomerCommand command(String email) {
        return new RegisterCustomerCommand("Synthetic", "Customer", email, "local-only-phone",
                "local-only-office", "local-only-crm-reference");
    }
}

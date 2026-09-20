package com.quinnbank.core.cif.application;

import com.quinnbank.core.cif.application.port.out.CustomerReadPort;
import com.quinnbank.core.cif.application.query.GetCustomerByIdQuery;
import com.quinnbank.core.cif.application.result.GetCustomerByIdResult;
import com.quinnbank.core.cif.application.service.CustomerQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerQueryServiceTest {
    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private CustomerReadPort customers;
    private CustomerQueryService service;

    @BeforeEach
    void setUp() {
        customers = mock(CustomerReadPort.class);
        service = new CustomerQueryService(customers);
    }

    @Test
    void returnsReadSnapshotWithoutReconstructingARegistrationAggregate() {
        GetCustomerByIdResult snapshot = new GetCustomerByIdResult(CUSTOMER_ID, "Synthetic",
                "Customer", "synthetic@example.invalid", "local-only-phone", "Synthetic Customer");
        when(customers.findById(CUSTOMER_ID)).thenReturn(Optional.of(snapshot));

        GetCustomerByIdResult result = service.getCustomerById(new GetCustomerByIdQuery(CUSTOMER_ID));

        assertThat(result).isEqualTo(snapshot);
        verify(customers).findById(CUSTOMER_ID);
    }

    @Test
    void preservesLegacyRecordedFullNameWhenStructuredNamesAreAbsent() {
        GetCustomerByIdResult legacySnapshot = new GetCustomerByIdResult(CUSTOMER_ID, null,
                null, null, null, "Synthetic Legacy Customer");
        when(customers.findById(CUSTOMER_ID)).thenReturn(Optional.of(legacySnapshot));

        GetCustomerByIdResult result = service.getCustomerById(new GetCustomerByIdQuery(CUSTOMER_ID));

        assertThat(result.firstName()).isNull();
        assertThat(result.lastName()).isNull();
        assertThat(result.fullName()).isEqualTo("Synthetic Legacy Customer");
    }

    @Test
    void reportsMissingCustomerWithoutExposingIdentifierInException() {
        when(customers.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCustomerById(new GetCustomerByIdQuery(CUSTOMER_ID)))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageNotContaining(CUSTOMER_ID.toString());
    }
}

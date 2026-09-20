package com.quinnbank.core.cif.domain;

import com.quinnbank.core.cif.domain.enums.CustomerStatus;
import com.quinnbank.core.cif.domain.enums.KycStatus;
import com.quinnbank.core.cif.domain.enums.RiskRating;
import com.quinnbank.core.cif.domain.exception.CustomerRegistrationRejectedException;
import com.quinnbank.core.cif.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTest {
    private static final LocalDateTime REGISTERED_AT = LocalDateTime.of(2026, 9, 20, 9, 30);

    @Test
    void registersPendingCustomerWithNormalizedProfileAndProvidedExternalReference() {
        Customer customer = Customer.register(" CIF-TEST-001 ", " Synthetic ", " Customer ",
                " SYNTHETIC@EXAMPLE.INVALID ", " local-only-phone ", " local-only-office ",
                " local-only-crm-reference ", REGISTERED_AT);

        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getCustomerNumber()).isEqualTo("CIF-TEST-001");
        assertThat(customer.getFirstName()).isEqualTo("Synthetic");
        assertThat(customer.getLastName()).isEqualTo("Customer");
        assertThat(customer.getFullName()).isEqualTo("Synthetic Customer");
        assertThat(customer.getEmail()).isEqualTo("synthetic@example.invalid");
        assertThat(customer.getPhone()).isEqualTo("local-only-phone");
        assertThat(customer.getOfficeId()).isEqualTo("local-only-office");
        assertThat(customer.getExternalId()).isEqualTo("local-only-crm-reference");
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.PENDING);
        assertThat(customer.getKycStatus()).isEqualTo(KycStatus.NOT_STARTED);
        assertThat(customer.getRiskRating()).isEqualTo(RiskRating.LOW);
        assertThat(customer.getCreatedAt()).isEqualTo(REGISTERED_AT);
        assertThat(customer.getUpdatedAt()).isEqualTo(REGISTERED_AT);
        assertThat(customer.getVersion()).isZero();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void preservesAbsentOptionalContactsAndExternalReferenceAsNull(String absent) {
        Customer customer = Customer.register("CIF-TEST-001", "Synthetic", "Customer",
                absent, absent, "local-only-office", absent, REGISTERED_AT);

        assertThat(customer.getEmail()).isNull();
        assertThat(customer.getPhone()).isNull();
        assertThat(customer.getExternalId()).isNull();
    }

    @Test
    @ResourceLock("java.util.Locale.default")
    void normalizesEmailIndependentlyOfDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            assertThat(Customer.normalizeEmail(" SYNTHETIC@EXAMPLE.INVALID "))
                    .isEqualTo("synthetic@example.invalid");
        } finally {
            Locale.setDefault(original);
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void rejectsMissingCustomerNumber(String absent) {
        assertThatThrownBy(() -> Customer.register(absent, "Synthetic", "Customer", null,
                null, "local-only-office", null, REGISTERED_AT))
                .isInstanceOf(CustomerRegistrationRejectedException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void rejectsMissingFirstName(String absent) {
        assertThatThrownBy(() -> Customer.register("CIF-TEST-001", absent, "Customer", null,
                null, "local-only-office", null, REGISTERED_AT))
                .isInstanceOf(CustomerRegistrationRejectedException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void rejectsMissingLastName(String absent) {
        assertThatThrownBy(() -> Customer.register("CIF-TEST-001", "Synthetic", absent, null,
                null, "local-only-office", null, REGISTERED_AT))
                .isInstanceOf(CustomerRegistrationRejectedException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void rejectsMissingOffice(String absent) {
        assertThatThrownBy(() -> Customer.register("CIF-TEST-001", "Synthetic", "Customer", null,
                null, absent, null, REGISTERED_AT))
                .isInstanceOf(CustomerRegistrationRejectedException.class);
    }

    @ParameterizedTest(name = "rejects {0} beyond its {1} character limit")
    @CsvSource({"customerNumber,50", "firstName,255", "lastName,255", "email,255",
            "phone,50", "officeId,50", "externalId,50"})
    void rejectsFieldsBeyondTheirStorageLimitsWithoutEchoingValues(String field, int limit) {
        String tooLong = "x".repeat(limit + 1);

        assertThatThrownBy(() -> Customer.register(
                field.equals("customerNumber") ? tooLong : "CIF-TEST-001",
                field.equals("firstName") ? tooLong : "Synthetic",
                field.equals("lastName") ? tooLong : "Customer",
                field.equals("email") ? tooLong : "synthetic@example.invalid",
                field.equals("phone") ? tooLong : null,
                field.equals("officeId") ? tooLong : "local-only-office",
                field.equals("externalId") ? tooLong : null,
                REGISTERED_AT))
                .isInstanceOf(CustomerRegistrationRejectedException.class)
                .hasMessageNotContaining(tooLong);
    }

    @Test
    void acceptsMaximumLengthStructuredNamesWithoutTruncatingFullName() {
        String firstName = "A".repeat(255);
        String lastName = "B".repeat(255);

        Customer customer = Customer.register("CIF-TEST-001", firstName, lastName, null,
                null, "local-only-office", null, REGISTERED_AT);

        assertThat(customer.getFirstName()).isEqualTo(firstName);
        assertThat(customer.getLastName()).isEqualTo(lastName);
        assertThat(customer.getFullName()).isEqualTo(firstName + " " + lastName).hasSize(511);
    }

    @Test
    void rejectsMissingRegistrationTime() {
        assertThatThrownBy(() -> Customer.register("CIF-TEST-001", "Synthetic", "Customer", null,
                null, "local-only-office", null, null))
                .isInstanceOf(CustomerRegistrationRejectedException.class);
    }
}

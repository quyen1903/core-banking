package com.quinnbank.core.cif.domain;

import com.quinnbank.core.cif.domain.enums.CustomerStatus;
import com.quinnbank.core.cif.domain.enums.KycStatus;
import com.quinnbank.core.cif.domain.enums.RiskRating;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTest {

    @Test
    void registerCreatesValidCustomerDefaults() {
        LocalDateTime registeredAt = LocalDateTime.of(2026, 6, 12, 0, 0);

        Customer customer = Customer.register(
                "CIF202606120000000001",
                "  Quinn Nguyen  ",
                "  QUINN@example.COM  ",
                "  0900000000  ",
                registeredAt
        );

        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getCustomerNumber()).isEqualTo("CIF202606120000000001");
        assertThat(customer.getFullName()).isEqualTo("Quinn Nguyen");
        assertThat(customer.getEmail()).isEqualTo("quinn@example.com");
        assertThat(customer.getPhone()).isEqualTo("0900000000");
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(customer.getKycStatus()).isEqualTo(KycStatus.NOT_STARTED);
        assertThat(customer.getRiskRating()).isEqualTo(RiskRating.LOW);
        assertThat(customer.getCreatedAt()).isEqualTo(registeredAt);
        assertThat(customer.getUpdatedAt()).isEqualTo(registeredAt);
        assertThat(customer.isActive()).isTrue();
    }

    @Test
    void registerRejectsBlankFullName() {
        assertThatThrownBy(() -> Customer.register(
                "CIF202606120000000001",
                " ",
                null,
                null,
                LocalDateTime.of(2026, 6, 12, 0, 0)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("full name is required");
    }
}

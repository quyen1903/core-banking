package com.quinnbank.core.cif.infrastructure.persistence;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.sql.DriverManager;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "CIF_TEST_DB_PORT", matches = "[0-9]+")
class CustomerMigrationIntegrationTest {
    @Test
    void upgradePreservesLegacyNamesAndReferencesWithoutGuessingNameParts() throws Exception {
        String url = "jdbc:postgresql://127.0.0.1:" + System.getenv("CIF_TEST_DB_PORT") + "/cif_refactor_test";
        String schema = "cif_upgrade_" + UUID.randomUUID().toString().replace("-", "");
        var legacy = Flyway.configure().dataSource(url, "cif_test", "local-only")
                .locations("classpath:db/migration").schemas(schema).defaultSchema(schema).target("6").load();
        legacy.migrate();
        UUID customerId = UUID.randomUUID();
        try (var connection = DriverManager.getConnection(url, "cif_test", "local-only")) {
            connection.setSchema(schema);
            try (var insert = connection.prepareStatement("""
                    insert into customers (id, customer_number, full_name, status, created_at, updated_at)
                    values (?, 'CIF-LEGACY', 'Synthetic Legacy Name', 'ACTIVE', current_timestamp, current_timestamp)
                    """)) {
                insert.setObject(1, customerId);
                assertEquals(1, insert.executeUpdate());
            }
            try (var insert = connection.createStatement()) {
                insert.executeUpdate("""
                        insert into account_products
                        (id, code, name, currency, created_at, updated_at)
                        values ('00000000-0000-0000-0000-000000000011', 'LOCAL', 'Synthetic Product',
                        'USD', current_timestamp, current_timestamp)
                        """);
            }
            try (var insert = connection.prepareStatement("""
                    insert into bank_accounts
                    (id, account_number, customer_id, product_id, currency, status, created_at,
                     updated_at, opened_at, opening_idempotency_key, opening_request_fingerprint)
                    values ('00000000-0000-0000-0000-000000000012', 'LOCAL-ACCOUNT', ?,
                    '00000000-0000-0000-0000-000000000011', 'USD', 'OPEN', current_timestamp,
                    current_timestamp, current_timestamp, 'local-migration-test', 'local-only')
                    """)) {
                insert.setObject(1, customerId);
                assertEquals(1, insert.executeUpdate());
            }
        }
        var upgraded = Flyway.configure().dataSource(url, "cif_test", "local-only")
                .locations("classpath:db/migration").schemas(schema).defaultSchema(schema).load();
        upgraded.migrate();
        assertTrue(upgraded.validateWithResult().validationSuccessful);
        try (var connection = DriverManager.getConnection(url, "cif_test", "local-only")) {
            connection.setSchema(schema);
            try (var statement = connection.createStatement();
                 var result = statement.executeQuery("""
                         select c.*, a.customer_id as account_customer_id from customers c
                         join bank_accounts a on a.customer_id = c.id
                         """)) {
                assertTrue(result.next());
                assertEquals(customerId, result.getObject("id", UUID.class));
                assertEquals(customerId, result.getObject("account_customer_id", UUID.class));
                assertEquals("Synthetic Legacy Name", result.getString("full_name"));
                assertNull(result.getString("first_name"));
                assertNull(result.getString("last_name"));
                assertNull(result.getString("office_id"));
                assertNull(result.getString("external_id"));
                assertEquals("ACTIVE", result.getString("status"));
                assertEquals(0L, result.getLong("version"));
            }
        }
    }
}

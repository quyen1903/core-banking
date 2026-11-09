package com.quinnbank.core.auth.adapter.out.persistence;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

/** Owns only the synthetic tables in a fresh schema; never uses the application's schema. */
final class AuthTransactionTestDatabase implements AutoCloseable {
    private final DataSource dataSource;
    private final String schema = "auth_tx_" + UUID.randomUUID().toString().replace("-", "");
    private boolean created;

    private AuthTransactionTestDatabase(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    static AuthTransactionTestDatabase create() throws IOException, SQLException {
        var dataSource = configuredDataSource();
        var database = new AuthTransactionTestDatabase(dataSource);
        var script = new ClassPathResource("auth-transaction-schema.sql")
                .getContentAsString(StandardCharsets.UTF_8).replace("${schema}", database.schema);
        try (var connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                ScriptUtils.executeSqlScript(connection,
                        new ByteArrayResource(script.getBytes(StandardCharsets.UTF_8)));
                connection.commit();
                database.created = true;
            } catch (SQLException | RuntimeException failure) {
                connection.rollback();
                throw failure;
            }
        }
        return database;
    }

    private static DriverManagerDataSource configuredDataSource() throws IOException {
        String port = System.getenv("AUTH_TEST_DB_PORT");
        if (port != null && !port.isBlank()) {
            int number = Integer.parseInt(port);
            if (number < 1 || number > 65535) {
                throw new IllegalArgumentException("AUTH_TEST_DB_PORT must be a valid TCP port");
            }
            return new DriverManagerDataSource("jdbc:postgresql://127.0.0.1:" + number
                    + "/auth_transaction_test?connectTimeout=10&socketTimeout=30", "auth_test", "local-only");
        }

        var properties = new Properties();
        var file = Path.of(".env");
        if (Files.exists(file)) {
            try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                // Same properties-file format used by the application's .env import.
                properties.load(reader);
            }
        }
        String url = required(properties, "CONNECTION_STRING");
        if (!url.startsWith("jdbc:postgresql://") || url.matches("(?i).*[?&](user|password)=.*")) {
            throw new IllegalArgumentException("Use a PostgreSQL JDBC URL and separate DB_USERNAME/DB_PASSWORD");
        }
        boolean loopback = url.matches("jdbc:postgresql://(localhost|127\\.0\\.0\\.1|\\[::1\\])(:[0-9]+)?/.*");
        if (!loopback && !url.matches(".*[?&]sslmode=(require|verify-ca|verify-full)(&.*)?$")) {
            throw new IllegalArgumentException("Remote transaction tests require TLS in CONNECTION_STRING");
        }
        var source = new DriverManagerDataSource(url,
                required(properties, "DB_USERNAME"), required(properties, "DB_PASSWORD"));
        var connectionProperties = new Properties();
        connectionProperties.setProperty("connectTimeout", "10");
        connectionProperties.setProperty("socketTimeout", "30");
        source.setConnectionProperties(connectionProperties);
        return source;
    }

    private static String required(Properties file, String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            value = file.getProperty(name);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing test connection setting: " + name);
        }
        return value;
    }

    DataSource dataSource() {
        return dataSource;
    }

    String schema() {
        return schema;
    }

    String table(String name) {
        if (!name.equals("identity_user") && !name.equals("identity_credential")) {
            throw new IllegalArgumentException("Unexpected test table");
        }
        return schema + "." + name;
    }

    @Override
    public void close() throws SQLException {
        if (!created) {
            return;
        }
        try (var connection = dataSource.getConnection(); var statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            try {
                statement.execute("drop table " + table("identity_credential"));
                statement.execute("drop table " + table("identity_user"));
                // No CASCADE: unexpected external dependencies cause teardown to fail safely.
                statement.execute("drop schema " + schema);
                connection.commit();
                created = false;
            } catch (SQLException failure) {
                connection.rollback();
                throw failure;
            }
        }
    }
}

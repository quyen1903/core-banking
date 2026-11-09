package com.quinnbank.core.auth.adapter.out.persistence;

import com.quinnbank.core.auth.adapter.out.configuration.AuthUseCaseConfiguration;
import com.quinnbank.core.auth.adapter.out.persistence.identity_credential.IdentityCredentialJpaEntity;
import com.quinnbank.core.auth.adapter.out.persistence.identity_credential.IdentityCredentialJpaRepository;
import com.quinnbank.core.auth.adapter.out.persistence.identity_credential.IdentityCredentialWritePersistenceAdapter;
import com.quinnbank.core.auth.adapter.out.persistence.identity_user.IdentityUserJpaEntity;
import com.quinnbank.core.auth.adapter.out.persistence.identity_user.IdentityUserJpaRepository;
import com.quinnbank.core.auth.adapter.out.persistence.identity_user.IdentityUserWritePersistenceAdapter;
import com.quinnbank.core.auth.application.command.RegisterIdentityUserCommand;
import com.quinnbank.core.auth.application.port.in.CommandAuthUseCase;
import com.quinnbank.core.auth.application.port.out.PasswordHashingPort;
import com.quinnbank.core.auth.domain.enums.IdentityOwnerType;
import com.quinnbank.core.auth.domain.enums.IdentityStatus;
import com.quinnbank.core.auth.domain.model.IdentityCredential;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Uses an isolated PostgreSQL schema; see docs/operations/auth-transaction-tests.md.
 * No test-managed transaction: commit/rollback must come from the production use-case decorator.
 */
@SpringJUnitConfig(AuthRegistrationTransactionTest.PersistenceTestConfiguration.class)
@EnabledIfEnvironmentVariable(named = "AUTH_TRANSACTION_TEST", matches = "true")
@MockitoSpyBean(types = IdentityCredentialWritePersistenceAdapter.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Execution(ExecutionMode.SAME_THREAD)
class AuthRegistrationTransactionTest {
    private final CommandAuthUseCase register;
    private final IdentityCredentialWritePersistenceAdapter credentials;
    private final PasswordHashingPort hashing;
    private final JdbcTemplate jdbc;
    private final AuthTransactionTestDatabase database;

    @Autowired
    AuthRegistrationTransactionTest(
        CommandAuthUseCase register,
        IdentityCredentialWritePersistenceAdapter credentials,
        PasswordHashingPort hashing, 
        JdbcTemplate jdbc,
        AuthTransactionTestDatabase database
    ) {
        this.register = register;
        this.credentials = credentials;
        this.hashing = hashing;
        this.jdbc = jdbc;
        this.database = database;
    }

    @BeforeEach
    void prepareIsolatedDatabase() {
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        assertTrue(AopUtils.isAopProxy(register), "Use the real transactional use-case bean");
        jdbc.update("delete from " + database.table("identity_credential"));
        jdbc.update("delete from " + database.table("identity_user"));
        reset(hashing);
        // This suite tests transactions and mappings, not the password algorithm.
        when(hashing.encode(anyString())).thenReturn("synthetic-encoded-password");
    }

    @AfterAll
    static void removeTestSchema(@Autowired AuthTransactionTestDatabase database) throws SQLException {
        // Explicit teardown makes a cleanup failure fail the suite, rather than only log a warning.
        database.close();
    }

    @Test
    void successfulRegistrationCommitsUserAndCredentialWithSharedPrimaryKey() {
        var command = command();

        var result = register.register(command);

        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        assertEquals(IdentityStatus.PENDING_ACTIVATION, result.status());
        assertRowCounts(1, 1);
        assertEquals(1, jdbc.queryForObject("""
            select count(*) from %s u
            join %s c on c.user_id = u.id
            where u.username_normalized = ? and u.owner_id = ?
            """.formatted(database.table("identity_user"), database.table("identity_credential")),
            Integer.class, command.username(), command.ownerId()
        ));
    }

    @Test
    void credentialDatabaseFailureRollsBackPreviouslyFlushedUser() {
        // Exceed the credential column's 512-character limit to trigger a real DB failure.
        when(hashing.encode(anyString())).thenReturn("x".repeat(513));
        doAnswer(invocation -> {
            assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            assertRowCounts(1, 0); // User INSERT has already reached PostgreSQL.
            return invocation.callRealMethod();
        }).when(credentials).save(any(IdentityCredential.class));

        var failure = assertThrows(RuntimeException.class, () -> register.register(command()));

        var sqlFailure = assertInstanceOf(SQLException.class,
                NestedExceptionUtils.getMostSpecificCause(failure));
        assertEquals("22001", sqlFailure.getSQLState()); // string_data_right_truncation
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        assertRowCounts(0, 0);
    }

    @Test
    void failureAfterBothFlushesRollsBackBothRows() {
        doAnswer(invocation -> {
            invocation.callRealMethod(); // Keep the real mapper, repository and saveAndFlush.
            assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            assertRowCounts(1, 1);
            throw new SyntheticRegistrationFailure();
        }).when(credentials).save(any(IdentityCredential.class));

        assertThrows(SyntheticRegistrationFailure.class, () -> register.register(command()));

        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        assertRowCounts(0, 0);
    }

    private void assertRowCounts(int users, int credentials) {
        assertEquals(users, jdbc.queryForObject("select count(*) from " + database.table("identity_user"), Integer.class));
        assertEquals(credentials, jdbc.queryForObject("select count(*) from " + database.table("identity_credential"), Integer.class));
    }

    private static RegisterIdentityUserCommand command() {
        return new RegisterIdentityUserCommand("synthetic." + UUID.randomUUID(),
                "local-only-password", IdentityOwnerType.CUSTOMER, UUID.randomUUID());
    }

    private static final class SyntheticRegistrationFailure extends RuntimeException {
        SyntheticRegistrationFailure() {
            super("Synthetic failure after credential flush");
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableTransactionManagement
    @EnableJpaRepositories(basePackageClasses = {
            IdentityUserJpaRepository.class, IdentityCredentialJpaRepository.class
    })
    @Import({AuthUseCaseConfiguration.class, IdentityUserWritePersistenceAdapter.class,
            IdentityCredentialWritePersistenceAdapter.class})
    static class PersistenceTestConfiguration {
        @Bean(destroyMethod = "close")
        AuthTransactionTestDatabase testDatabase() throws java.io.IOException, SQLException {
            return AuthTransactionTestDatabase.create();
        }

        @Bean
        DataSource dataSource(AuthTransactionTestDatabase database) {
            return database.dataSource();
        }

        @Bean
        LocalContainerEntityManagerFactoryBean entityManagerFactory(
                DataSource dataSource, AuthTransactionTestDatabase database) {
            var factory = new LocalContainerEntityManagerFactoryBean();
            factory.setDataSource(dataSource);
            factory.setPackagesToScan(IdentityUserJpaEntity.class.getPackageName(),
                    IdentityCredentialJpaEntity.class.getPackageName());
            factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            factory.setJpaPropertyMap(Map.of(
                    "hibernate.hbm2ddl.auto", "validate",
                    "hibernate.default_schema", database.schema(),
                    "hibernate.jdbc.time_zone", "UTC"));
            return factory;
        }

        @Bean
        JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
            return new JpaTransactionManager(entityManagerFactory);
        }

        @Bean
        JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        PasswordHashingPort passwordHashingPort() {
            return mock(PasswordHashingPort.class);
        }
    }
}

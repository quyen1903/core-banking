package com.quinnbank.core.cif.infrastructure.persistence;

import com.quinnbank.core.cif.application.DuplicateCustomerEmailException;
import com.quinnbank.core.cif.application.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.port.in.GetCustomerByIdUseCase;
import com.quinnbank.core.cif.application.port.in.RegisterCustomerUseCase;
import com.quinnbank.core.cif.application.port.out.CustomerReadPort;
import com.quinnbank.core.cif.application.port.out.CustomerWritePort;
import com.quinnbank.core.cif.application.query.GetCustomerByIdQuery;
import com.quinnbank.core.cif.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.config.location=classpath:cif-integration.yml")
@EnabledIfEnvironmentVariable(named = "CIF_TEST_DB_PORT", matches = "[0-9]+")
class CustomerPersistenceIntegrationTest {
    @Autowired RegisterCustomerUseCase register;
    @Autowired GetCustomerByIdUseCase query;
    @Autowired CustomerWritePort writes;
    @Autowired CustomerReadPort reads;
    @Autowired JdbcTemplate jdbc;
    @Autowired PlatformTransactionManager transactionManager;

    @Test
    void registrationRoundTripsMetadataAndReadProjection() {
        String email = UUID.randomUUID() + "@example.invalid";
        var result = register.registerCustomer(new RegisterCustomerCommand(
                " Synthetic ", " Customer ", " " + email.toUpperCase(java.util.Locale.ROOT) + " ",
                " local-only ", " local-office ", " source-reference "));
        var customer = query.getCustomerById(new GetCustomerByIdQuery(result.customerId()));
        assertEquals("PENDING", result.status());
        assertEquals("Synthetic", customer.firstName());
        assertEquals("Customer", customer.lastName());
        assertEquals("Synthetic Customer", customer.fullName());
        assertEquals(email, customer.email());
        assertEquals("local-only", customer.phoneNumber());
        var row = jdbc.queryForMap("select * from customers where id = ?", result.customerId());
        assertEquals("local-office", row.get("office_id"));
        assertEquals("source-reference", row.get("external_id"));
        assertEquals(0L, ((Number) row.get("version")).longValue());
        assertNotNull(row.get("created_at"));
        assertEquals(row.get("created_at"), row.get("updated_at"));
    }

    @Test
    void absentEmailCanBeUsedByMoreThanOneCustomer() {
        var first = register.registerCustomer(command(null));
        var second = register.registerCustomer(command("  "));
        assertNotEquals(first.customerId(), second.customerId());
        assertNull(reads.findById(first.customerId()).orElseThrow().email());
        assertNull(reads.findById(second.customerId()).orElseThrow().email());
    }

    @Test
    void normalizedDuplicateIsRejectedWithoutExposingEmail() {
        String email = UUID.randomUUID() + "@example.invalid";
        register.registerCustomer(command(email));
        var failure = assertThrows(DuplicateCustomerEmailException.class,
                () -> register.registerCustomer(command(" " + email.toUpperCase(java.util.Locale.ROOT) + " ")));
        assertFalse(failure.getMessage().contains(email));
        assertEquals(1, jdbc.queryForObject(
                "select count(*) from customers where email = ?", Integer.class, email));
    }

    @Test
    void commandJoinsOuterTransactionAndRollsBack() {
        assertTrue(AopUtils.isAopProxy(register));
        String email = UUID.randomUUID() + "@example.invalid";
        var transaction = new TransactionTemplate(transactionManager);
        assertThrows(IllegalStateException.class, () -> transaction.executeWithoutResult(status -> {
            assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            register.registerCustomer(command(email));
            assertEquals(1, jdbc.queryForObject(
                    "select count(*) from customers where email = ?", Integer.class, email));
            throw new IllegalStateException("synthetic downstream failure");
        }));
        assertEquals(0, jdbc.queryForObject(
                "select count(*) from customers where email = ?", Integer.class, email));
    }

    @Test
    void concurrentEmailInsertHasOneWinnerAndSafeConflict() throws Exception {
        String email = UUID.randomUUID() + "@example.invalid";
        var barrier = new CyclicBarrier(2);
        Callable<String> attempt = () -> {
            barrier.await(10, TimeUnit.SECONDS);
            try {
                new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                        writes.save(customer("CIF-" + UUID.randomUUID(), email)));
                return "saved";
            } catch (DuplicateCustomerEmailException failure) {
                assertFalse(failure.getMessage().contains(email));
                return "duplicate";
            }
        };
        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(attempt);
            var second = executor.submit(attempt);
            assertEquals(java.util.Set.of("saved", "duplicate"),
                    java.util.Set.of(first.get(20, TimeUnit.SECONDS), second.get(20, TimeUnit.SECONDS)));
        }
        assertEquals(1, jdbc.queryForObject(
                "select count(*) from customers where email = ?", Integer.class, email));
    }

    @Test
    void customerNumberConflictIsNotMisreportedAsEmailConflict() {
        String number = "CIF-" + UUID.randomUUID();
        var transaction = new TransactionTemplate(transactionManager);
        transaction.executeWithoutResult(status -> writes.save(customer(number, null)));
        assertThrows(DataIntegrityViolationException.class, () ->
                transaction.executeWithoutResult(status -> writes.save(customer(number, null))));
    }

    @Test
    void readOnlyQueryWrapperDeclaresAndEnforcesTransaction() {
        assertTrue(AopUtils.isAopProxy(query));
        var probe = new com.quinnbank.core.cif.infrastructure.configuration.TransactionalGetCustomerByIdUseCase(q -> {
            assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            assertTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            return reads.findById(q.customerId()).orElseThrow();
        });
        var proxyFactory = new org.springframework.aop.framework.ProxyFactory(probe);
        var transactionAdvice = new org.springframework.transaction.interceptor.TransactionInterceptor();
        transactionAdvice.setTransactionManager(transactionManager);
        transactionAdvice.setTransactionAttributeSource(
                new org.springframework.transaction.annotation.AnnotationTransactionAttributeSource());
        proxyFactory.addAdvice(transactionAdvice);
        var proxied = (GetCustomerByIdUseCase) proxyFactory.getProxy();
        var result = register.registerCustomer(command(null));
        assertEquals(result.customerId(), proxied.getCustomerById(
                new GetCustomerByIdQuery(result.customerId())).customerId());
    }

    private static RegisterCustomerCommand command(String email) {
        return new RegisterCustomerCommand("Synthetic", "Customer", email, null, "local-office", null);
    }

    private static Customer customer(String number, String email) {
        return Customer.register(number, "Synthetic", "Customer", email, null,
                "local-office", null, LocalDateTime.of(2026, 9, 20, 0, 0));
    }
}

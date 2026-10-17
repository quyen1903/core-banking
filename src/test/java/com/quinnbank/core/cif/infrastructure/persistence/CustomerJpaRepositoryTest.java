package com.quinnbank.core.cif.infrastructure.persistence;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CustomerJpaRepositoryTest {
    @Test
    void repositoryQueriesMatchEntityAttributesAndResultConstructor() {
        var registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", false)
                .applySetting("hibernate.hbm2ddl.auto", "none")
                .build();
        try (var factory = new MetadataSources(registry)
                .addAnnotatedClass(CustomerJpaEntity.class)
                .buildMetadata()
                .buildSessionFactory();
             var session = factory.openSession()) {
            assertDoesNotThrow(() -> new JpaRepositoryFactory(session)
                    .getRepository(CustomerJpaRepository.class));
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}

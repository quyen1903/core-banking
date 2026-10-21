package com.quinnbank.core.auth.adapter.out.persistence;

import com.quinnbank.core.auth.adapter.out.persistence.identity_credential.IdentityCredentialJpaEntity;
import com.quinnbank.core.auth.adapter.out.persistence.identity_login_state.IdentityLoginStateJpaEntity;
import com.quinnbank.core.auth.adapter.out.persistence.identity_user.IdentityUserJpaEntity;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AuthPersistenceMappingTest {
    private static StandardServiceRegistry registry;
    private static Metadata metadata;
    private static SessionFactory sessionFactory;
    private static String schema;

    @BeforeAll
    static void buildPostgresMappingsWithoutConnectingToADatabase() {
        var script = new StringWriter();
        registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", false)
                .applySetting("jakarta.persistence.schema-generation.database.action", "none")
                .applySetting("jakarta.persistence.schema-generation.scripts.action", "create")
                .applySetting("jakarta.persistence.schema-generation.scripts.create-target", script)
                .build();
        metadata = new MetadataSources(registry)
                .addAnnotatedClass(IdentityUserJpaEntity.class)
                .addAnnotatedClass(IdentityCredentialJpaEntity.class)
                .addAnnotatedClass(IdentityLoginStateJpaEntity.class)
                .buildMetadata();
        sessionFactory = metadata.buildSessionFactory();
        schema = script.toString();
    }

    @AfterAll
    static void closeMappings() {
        try {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        } finally {
            if (registry != null) {
                StandardServiceRegistryBuilder.destroy(registry);
            }
        }
    }

    @Test
    void userMappingSeparatesCredentialAndUsesStableIdentityAndCanonicalUsername() {
        Table user = table(IdentityUserJpaEntity.class);

        assertThat(user.getName()).isEqualTo("identity_user");
        assertThat(user.getColumns()).extracting(Column::getName)
                .contains("public_id", "username_normalized", "owner_type", "owner_id", "security_version")
                .doesNotContain("password_hash", "firsttime_login_remaining", "nonlocked", "enabled");
        assertThat(metadata.getEntityBinding(IdentityUserJpaEntity.class.getName())
                .getIdentifier().getType().getReturnedClass()).isEqualTo(Long.class);
        assertThat(user.getUniqueKeys().get("uq_identity_user_username").getColumns())
                .extracting(Column::getName).containsExactly("username_normalized");
        assertThat(user.getUniqueKeys().get("uq_identity_user_public_id").getColumns())
                .extracting(Column::getName).containsExactly("public_id");
        assertThat(schema).contains("collate \"C\"", "username_normalized ~ '^[a-z0-9._@+-]+$'");
    }

    @Test
    void credentialAndLoginStateShareTheUserPrimaryKeyWithoutCascadingIdentityDeletion() {
        for (Class<?> entity : new Class<?>[]{IdentityCredentialJpaEntity.class, IdentityLoginStateJpaEntity.class}) {
            Table dependent = table(entity);
            assertThat(dependent.getPrimaryKey().getColumns())
                    .extracting(Column::getName).containsExactly("user_id");
            assertThat(dependent.getForeignKeyCollection()).singleElement().satisfies(foreignKey -> {
                assertThat(foreignKey.getReferencedTable().getName()).isEqualTo("identity_user");
                assertThat(foreignKey.getColumns()).extracting(Column::getName).containsExactly("user_id");
            });
        }

        assertThat(table(IdentityCredentialJpaEntity.class).getColumn(new Column("password_hash")))
                .satisfies(column -> {
                    assertThat(column.isNullable()).isFalse();
                    assertThat(column.getLength()).isEqualTo(512L);
                });
        assertThat(schema).doesNotContain("on delete cascade");
    }

    @Test
    void securityTimestampsUseInstantsAndMutableTablesHaveOptimisticLocks() {
        for (Class<?> entity : new Class<?>[]{IdentityUserJpaEntity.class,
                IdentityCredentialJpaEntity.class, IdentityLoginStateJpaEntity.class}) {
            var binding = metadata.getEntityBinding(entity.getName());
            assertThat(binding.getProperty("createdAt").getType().getReturnedClass()).isEqualTo(Instant.class);
            assertThat(binding.getProperty("updatedAt").getType().getReturnedClass()).isEqualTo(Instant.class);
            assertThat(binding.getVersion()).isNotNull();
            assertThat(binding.getVersion().getName()).isEqualTo("version");
        }

        assertThat(metadata.getEntityBinding(IdentityCredentialJpaEntity.class.getName())
                .getProperty("passwordChangedAt").getType().getReturnedClass()).isEqualTo(Instant.class);
        assertThat(schema).contains("timestamp(6) with time zone");
    }

    @Test
    void generatedSchemaIncludesRejectionConstraintsForInvalidSecurityState() {
        assertThat(schema).contains(
                "ck_identity_credential_hash check (length(btrim(password_hash)) > 0)",
                "ck_identity_user_security_version check (security_version >= 0)",
                "ck_identity_login_state_failed_attempts check (failed_attempts >= 0)",
                "ck_identity_login_state_failure_window check",
                "last_failed_at >= failure_window_started_at");
        assertThat(schema).contains("'PENDING_ACTIVATION'", "'ACTIVE'", "'DISABLED'", "'STAFF'");
        assertThat(schema).doesNotContain("'LOCKED'");
    }

    private static Table table(Class<?> entity) {
        return metadata.getEntityBinding(entity.getName()).getTable();
    }
}

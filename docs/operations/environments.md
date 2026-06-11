# QuinnBank Environment Standard

> Entry order: `AGENTS.md` -> `SECURITY.md` -> `LIBRARY.md` ->
> `CODING_STANDARD.md` -> this file.

---

## 1. Purpose

This document defines the initial environment model for QuinnBank Core.

Environment configuration is part of the banking control surface. It must not
contain production secrets, uncontrolled local shortcuts, or settings that
weaken financial integrity, auditability, or security.

---

## 2. Supported Environments

| Tier | Spring profile | Alias profile | Purpose |
| --- | --- | --- | --- |
| Development | `dev` | none | Local/shared development with synthetic data only |
| Test/QA | `test` | `qa` | Automated testing, QA verification, integration checks |
| UAT | `uat` | none | Business/user acceptance with controlled synthetic or approved masked data |
| Staging/Pre-prod | `staging` | `pre-prod` | Production-like verification before release |
| Production | `prod` | none | Live banking production workload |

Aliases are supported so teams may use either:

```powershell
$env:SPRING_PROFILES_ACTIVE = "test"
$env:SPRING_PROFILES_ACTIVE = "qa"
$env:SPRING_PROFILES_ACTIVE = "staging"
$env:SPRING_PROFILES_ACTIVE = "pre-prod"
```

---

## 3. Configuration Files

Common configuration:

```text
src/main/resources/application.yml
```

Environment-specific configuration:

```text
src/main/resources/application-dev.yml
src/main/resources/application-test.yml
src/main/resources/application-qa.yml
src/main/resources/application-uat.yml
src/main/resources/application-staging.yml
src/main/resources/application-pre-prod.yml
src/main/resources/application-prod.yml
```

`application.properties` is intentionally not used. Keeping both `.properties`
and `.yml` for the same keys makes precedence harder to audit.

---

## 4. Activation

Local development example:

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev"
$env:QUINNBANK_DEV_DB_URL = "jdbc:postgresql://localhost:5432/quinnbank"
$env:QUINNBANK_DEV_DB_USERNAME = "postgres"
$env:QUINNBANK_DEV_DB_PASSWORD = "postgres"
.\gradlew.bat bootRun
```

Local `.env` is also supported through `spring.config.import`. The real `.env`
file is ignored by git and is for local development only. `.env.example` is
tracked as the local PostgreSQL template.

Production-style activation example:

```powershell
$env:SPRING_PROFILES_ACTIVE = "prod"
$env:QUINNBANK_PROD_DB_URL = "jdbc:postgresql://prod-db.internal:5432/quinnbank"
$env:QUINNBANK_PROD_DB_USERNAME = "service_identity"
$env:QUINNBANK_PROD_DB_PASSWORD = "<loaded-from-secret-manager>"
.\gradlew.bat bootRun
```

Production secrets must be injected by approved runtime secret management. They
must not be stored in source, local shell history, CI logs, screenshots, or
documentation.

---

## 5. Required Database Variables

Each environment supports environment-specific variables first, then shared
fallbacks where appropriate.

| Profile | URL | Username | Password |
| --- | --- | --- | --- |
| `dev` | `QUINNBANK_DEV_DB_URL` or `QUINNBANK_DB_URL` | `QUINNBANK_DEV_DB_USERNAME` or `QUINNBANK_DB_USERNAME` | `QUINNBANK_DEV_DB_PASSWORD` or `QUINNBANK_DB_PASSWORD` |
| `test` | `QUINNBANK_TEST_DB_URL` or `QUINNBANK_DB_URL` | `QUINNBANK_TEST_DB_USERNAME` or `QUINNBANK_DB_USERNAME` | `QUINNBANK_TEST_DB_PASSWORD` or `QUINNBANK_DB_PASSWORD` |
| `qa` | `QUINNBANK_QA_DB_URL`, `QUINNBANK_TEST_DB_URL`, or `QUINNBANK_DB_URL` | `QUINNBANK_QA_DB_USERNAME`, `QUINNBANK_TEST_DB_USERNAME`, or `QUINNBANK_DB_USERNAME` | `QUINNBANK_QA_DB_PASSWORD`, `QUINNBANK_TEST_DB_PASSWORD`, or `QUINNBANK_DB_PASSWORD` |
| `uat` | `QUINNBANK_UAT_DB_URL` or `QUINNBANK_DB_URL` | `QUINNBANK_UAT_DB_USERNAME` or `QUINNBANK_DB_USERNAME` | `QUINNBANK_UAT_DB_PASSWORD` or `QUINNBANK_DB_PASSWORD` |
| `staging` | `QUINNBANK_STAGING_DB_URL` or `QUINNBANK_DB_URL` | `QUINNBANK_STAGING_DB_USERNAME` or `QUINNBANK_DB_USERNAME` | `QUINNBANK_STAGING_DB_PASSWORD` or `QUINNBANK_DB_PASSWORD` |
| `pre-prod` | `QUINNBANK_PRE_PROD_DB_URL`, `QUINNBANK_STAGING_DB_URL`, or `QUINNBANK_DB_URL` | `QUINNBANK_PRE_PROD_DB_USERNAME`, `QUINNBANK_STAGING_DB_USERNAME`, or `QUINNBANK_DB_USERNAME` | `QUINNBANK_PRE_PROD_DB_PASSWORD`, `QUINNBANK_STAGING_DB_PASSWORD`, or `QUINNBANK_DB_PASSWORD` |
| `prod` | `QUINNBANK_PROD_DB_URL` or `QUINNBANK_DB_URL` | `QUINNBANK_PROD_DB_USERNAME` or `QUINNBANK_DB_USERNAME` | `QUINNBANK_PROD_DB_PASSWORD` or `QUINNBANK_DB_PASSWORD` |

`prod`, `uat`, `staging`, and `pre-prod` must not rely on local defaults.
Shared `dev` environments should also override local defaults through managed
configuration or secret management.

---

## 6. Environment Data Rules

| Environment | Data rule |
| --- | --- |
| `dev` | Synthetic data only. Local throwaway credentials only. |
| `test` / `qa` | Synthetic data only unless approved masked data is explicitly allowed. |
| `uat` | Controlled synthetic or approved masked data. Business test data must be traceable and removable. |
| `staging` / `pre-prod` | Production-like configuration with synthetic or formally masked data. No uncontrolled production copy. |
| `prod` | Live data only under production controls. No local/manual secrets. |

Production data must not be copied into lower environments without formal data
governance, masking, access control, audit, and retention approval.

---

## 7. Common Safety Defaults

All environments inherit:

- `spring.jpa.hibernate.ddl-auto=validate`
- `spring.jpa.open-in-view=false`
- `spring.jpa.show-sql=false`
- Flyway enabled with migrations from `classpath:db/migration`
- Actuator exposure limited to `health,info`
- Health details hidden by default
- Graceful shutdown enabled
- Hibernate bind logging disabled by default

These defaults protect migration discipline, prevent accidental schema mutation,
avoid lazy-loading leaks, and reduce sensitive-data exposure through logs.

---

## 8. Operational Requirements

Before an environment is considered ready:

- Database credentials come from approved secret management.
- Service identity is least-privilege.
- Database user cannot perform broad administrative actions in application
  runtime.
- Flyway migration execution is controlled and auditable.
- Logs are routed to approved storage with redaction.
- Health endpoints do not expose sensitive internals.
- Network access is restricted to required dependencies.
- Backups and restore procedures exist for persistent environments.
- Release and rollback process is documented.

---

## 9. Current Schema Status

The initial entity and migration disagreed:

```text
Customer entity table: customer
Migration table:       customers
```

The CIF module now maps to `customers`, and
`V2__align_cif_customer_schema.sql` aligns the timestamp columns and CIF
lifecycle fields forward-only. Environment readiness still depends on each
environment having the approved PostgreSQL database, credentials, network
access, migration controls, and synthetic or governed data required by this
standard.

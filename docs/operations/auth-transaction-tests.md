# Auth registration transaction tests

`AuthRegistrationTransactionTest` runs the production `AuthUseCaseConfiguration`,
transaction decorator, persistence adapters, mappers and Spring Data repositories
against PostgreSQL. Only password hashing is mocked. A spy delegates to the real
credential adapter and injects a failure in the final test.

The three scenarios verify:

1. Successful registration commits one user and its credential with a shared PK.
2. PostgreSQL rejects an oversized synthetic password hash after the user has
   been flushed; the user insert is rolled back.
3. An unchecked exception after both inserts have been flushed rolls back both.

There is no `@Transactional` on the test and no enclosing `TransactionTemplate`.
Assertions after the use-case call read the database outside the transaction.
This prevents a test-managed transaction from hiding a missing use-case boundary.
See [Spring's explanation of test-managed transactions](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/tx.html).

## Run against the configured Neon development database

From the repository root in PowerShell:

```powershell
$env:AUTH_TRANSACTION_TEST = 'true'
.\gradlew.bat test --tests 'com.quinnbank.core.auth.adapter.out.persistence.AuthRegistrationTransactionTest' --rerun-tasks
Remove-Item Env:AUTH_TRANSACTION_TEST
```

The fixture reads `CONNECTION_STRING`, `DB_USERNAME`, and `DB_PASSWORD` from the
existing ignored `.env`, using the application's Java properties-file format.
Environment variables with those names take precedence. Do not put credentials
on the command line or in this document. The test does not bootstrap the full
application or load its `application.yml`.

Use an authorized development/test Neon database, not production. The database
role needs permission to create a schema. Remote connections require TLS;
the existing JDBC URL's TLS parameters are preserved.

Each run creates a fresh `auth_tx_<random UUID>` schema with two synthetic tables
from `src/test/resources/auth-transaction-schema.sql`. Hibernate uses `validate`
and this explicit default schema. Every JDBC query also qualifies its table
with that schema, so no query relies on the connection pool's `search_path`.
The application's existing auth tables are not cleared, dropped, or used.
See [PostgreSQL schema namespaces](https://www.postgresql.org/docs/current/ddl-schemas.html).

At teardown, the fixture drops its credential table, user table, then its schema
in a transaction, without `CASCADE`. Cleanup failure fails the suite. A forcibly
killed process or network outage can leave the synthetic schema behind; only
remove the generated schema belonging to that run after inspecting it.

The expected result is **3 tests, 0 failures, 0 skipped** in
`build/test-results/test/TEST-com.quinnbank.core.auth.adapter.out.persistence.AuthRegistrationTransactionTest.xml`.
Without `AUTH_TRANSACTION_TEST=true`, the suite is skipped; a successful Gradle
invocation in that case does **not** prove transaction behavior.

## Optional local PostgreSQL instead

If Docker is installed, start a disposable instance using the
[official PostgreSQL image](https://hub.docker.com/_/postgres):

```powershell
docker run --detach --rm --name quinnbank-auth-tx-test --publish 127.0.0.1:55440:5432 --env POSTGRES_DB=auth_transaction_test --env POSTGRES_USER=auth_test --env POSTGRES_PASSWORD=local-only postgres:17
docker exec quinnbank-auth-tx-test pg_isready -U auth_test -d auth_transaction_test
```

Once `pg_isready` reports that the server is accepting connections:

```powershell
$env:AUTH_TRANSACTION_TEST = 'true'
$env:AUTH_TEST_DB_PORT = '55440'
.\gradlew.bat test --tests 'com.quinnbank.core.auth.adapter.out.persistence.AuthRegistrationTransactionTest' --rerun-tasks
```

Setting `AUTH_TEST_DB_PORT` selects the fixed local test database and synthetic
credentials instead of `.env`. No Docker or Testcontainers dependency is
required by the Java tests. Leave this variable unset to use Neon.

After the Docker-based run, discard the test container and its temporary data:

```powershell
docker stop quinnbank-auth-tx-test
Remove-Item Env:AUTH_TEST_DB_PORT
Remove-Item Env:AUTH_TRANSACTION_TEST
```

## Scope

This suite verifies registration commit/rollback and the current entity mappings.
It does not verify migrations, concurrent registrations, HTTP authorization,
password strength/Argon2, or audit atomicity. Registration does not currently
persist an audit record. No runtime dependencies or authentication controls are
changed by these tests.

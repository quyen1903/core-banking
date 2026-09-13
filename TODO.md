# QuinnBank Core — Todo list

[Français](TODO.fr.md)

Updated: 13 September 2026. This backlog is based on the existing source code and documentation. The application and test suite were not run while preparing it. Verification tasks below do not imply that defects have been reproduced.

This document change is R0 (documentation). Each task's risk label describes implementation risk under [AGENTS.md](AGENTS.md). Mark a task complete only when evidence satisfies its acceptance criteria.

## Starting point

| Area | Found in the repository | Next steps |
| --- | --- | --- |
| CIF, employee, identity | Models, persistence, use cases and some unit tests | Complete workflows, authorization scope and integration tests |
| Account | Opening, queries, idempotency and balance projection | Ownership checks, account lifecycle and database verification |
| Ledger | Balanced journals, posting, replay/conflict handling and balance projection calls | Atomicity and concurrency tests, reversal and reconciliation |
| Security | Method security, API authorities; HTTP Basic and bootstrap user in `dev`/`test` | Customer authentication and security configuration for deployment environments |
| Customer web | Banking screens, API client and synthetic data | Authentication, dashboard, profile, transactions and transfers still use mocks/placeholders |
| Database / build | Flyway V1–V6, Gradle, environment configuration and frontend scripts | Validate migrations, reproducible builds/tests and release evidence |

Main sources: [architecture](docs/ARCHITECTURE.md), [account](docs/architecture/account-module-design.md), [identity](docs/architecture/identity-module-design.md), [environments](docs/operations/environments.md), [frontend](customer-web/README.md), [security configuration](src/main/java/com/quinnbank/core/security/SecurityConfiguration.java), [ledger service](src/main/java/com/quinnbank/core/ledger/application/service/PostLedgerJournalService.java).

## P0 — Foundations required before enabling live banking workflows

- [ ] **T01 · Establish the build/test baseline — R1.** Run backend tests against a dedicated local PostgreSQL test database with synthetic data; run frontend lint, typecheck and build. Done when commands, tool versions, results and outstanding failures are recorded; never skip failing tests to report success.
- [ ] **T02 · Document local setup — R0.** Expand the root README with the Java toolchain from `build.gradle`, Gradle wrapper, PostgreSQL, pnpm from `package.json`, environment variables, migrations and startup instructions for both applications. Done when a newcomer can follow it from a clean checkout using local configuration.
- [ ] **T03 · Design and implement authentication — R3.** Build on the identity foundation to implement login, refresh, logout and locked/disabled identity handling. Define sessions/tokens, expiration, rotation/revocation and CSRF/CORS for the chosen mechanism; do not invent token formats or store tokens in localStorage. Done when tests cover valid access, incorrect passwords, expiration, revocation and refresh-token replay where applicable.
- [ ] **T04 · Enforce resource authorization — R3.** Add application-flow policies for CIF, account, identity and employee: actor, owner, role and business branch/scope. Done when customer A cannot read/change customer B's resources, employees outside their scope are denied, and use cases invoked outside HTTP still enforce authorization.
- [ ] **T05 · Configure security by environment — R3.** Verify behavior outside `dev`/`test`, remove reliance on undesigned defaults and align bootstrap authorities with current permissions. Done when tests cover public/protected endpoints, bootstrap availability only in allowed environments, safe 401/403 responses and actuator responses without internal data exposure.
- [ ] **T06 · Audit sensitive actions — R3.** Design audit storage and add events for identity/role/credential, CIF and account actions, including actor, action, resource scope, decision, reason, UTC timestamp and correlation ID. Done when tests verify protection from unauthorized modification/deletion, sensitive-data redaction and audit-write failure behavior; domain events or application logs alone are insufficient evidence.
- [ ] **T07 · Prove ledger transaction atomicity — R4.** Add PostgreSQL integration tests for posting and balance projection within one transaction. Done when a single account-update or persistence failure rolls back both the journal and every balance change, leaving no partial posting.
- [ ] **T08 · Prove idempotency and concurrency behavior — R4.** Test account opening, posting and projection with concurrent requests, retries after timeout, the same key with different payloads, different actors sharing a key, lock contention and insufficient funds. Done when there is only one financial effect, key scope is explicit, valid retries return consistent results and conflicts have stable error codes.
- [ ] **T09 · Validate migrations and mappings — R4.** Run V1–V6 on a new database and verify upgrade paths with synthetic data; compare foreign keys, unique constraints/indexes, precision/scale, nullability, audit fields and versions. Done when Flyway and JPA validation pass and backfill/locking/deployment risks are assessed; fix errors with new migrations if older ones have reached shared environments.

## P1 — Complete the workflows already represented in the UI

- [ ] **T10 · Connect frontend login — R3; after T03–T05.** Replace `DEMO_SESSION` in `customer-web/api/auth.ts` with backend authentication. Done when login/logout/expiration work, private cached data is cleared when users change, and demo mode is clearly identifiable.
- [ ] **T11 · Account list and dashboard APIs — R3; after T04.** Provide queries restricted to the actor's account scope, pagination where needed and balance summaries per currency. Done when mocks in `accounts.ts`/`dashboard.ts` are replaced, cross-customer access tests pass and currencies are not combined into one balance without conversion.
- [ ] **T12 · Align profile and CIF flows — R3; after T04, T06.** Identify the active CIF flow across `adapter`/`infrastructure` packages and similarly named contracts/models before standardizing them. Connect profile queries to the correct actor; allow updates only to permitted fields. Done when contracts are explicit, PII is minimized, and validation and mass-assignment protection tests pass.
- [ ] **T13 · Account products and lifecycle — R4; after T04, T06–T09.** Define product rules, eligibility, block/unblock/close behavior and approval authority; implement transitions through the domain. Done when tests cover remaining balances/obligations under the agreed policy, idempotency, version conflicts, audit and forbidden transitions.
- [ ] **T14 · Design internal transfers — R4; after T07–T09.** Define the owning module, actor, source/destination accounts, limits, currency, fees if any, value/posting dates, states and authentication/approval steps. Done when a reviewed design, API contract and failure/retry/reversal flows exist; do not invent business limits that have not been decided.
- [ ] **T15 · Implement transfers through the ledger — R4; after T03–T09, T14.** Add a transfer use case and endpoint that call the ledger through an application contract. Done when tests cover authorization, insufficient funds, invalid accounts, currency mismatch, duplicates, concurrent debits, timeouts and rollback; no direct balance changes outside the designed ledger/projection flow.
- [ ] **T16 · Connect the transfer screen — R4; after T10, T15.** Replace the placeholder in `customer-web/api/transfers.ts`; retain the idempotency key when retrying the same request and query status when the outcome is uncertain. Done when the UI reflects backend state without treating a timeout as definitive failure or independently reporting transfer success.
- [ ] **T17 · Transaction history — R3; after T04 and the ledger/transfer contract.** Add queries with ownership scope, pagination, date and status filters; connect `customer-web/api/transactions.ts` and transfer history. Done when records trace to journals/transfers, dates/times/currencies are explicit and tests prevent cross-customer data exposure.
- [ ] **T18 · Define the frontend/backend money contract — R4; before T15–T17.** Review DTOs, schemas, forms and formatting; use exact decimal representations, explicit currency, defined scale/rounding and backend calculations using `Money`/`BigDecimal`. Done when tests cover fractions, precision limits, invalid payloads and lossless round trips; do not use binary floating point for monetary values.
- [ ] **T19 · Test customer workflows — R3; after T10–T18.** Test login → account view → transfer → result/history, plus session expiration, denials, backend disconnection and resubmission. Done when workflows pass with synthetic data; evaluate any new testing tools under `LIBRARY.md` first, since the frontend currently has no test script.

## P2 — Recovery, reconciliation and operational controls

- [ ] **T20 · Reversal linked to the original journal — R4; after T07–T09.** Design reversal/adjustment entries with reasons, permissions and appropriate approvals. Done when the original journal remains unchanged, the reversal balances and links to its source, and retries cannot reverse twice.
- [ ] **T21 · Reconcile ledger and balance projections — R4; after T07–T09.** Build queries/procedures to check discrepancies by account and currency. Done when tests detect synthetic discrepancies and alerts/investigation procedures exist; do not automatically correct balances without evidence. If using a job, document its trigger, identity, retries and rerun behavior.
- [ ] **T22 · MFA, recovery and abuse controls — R3; after T03, T06.** Implement MFA for actors/actions required by `SECURITY.md`, password/MFA reset, rate limits and login-failure monitoring. Done when tests cover bypass attempts, expired/reused tokens, locked identities and audit; finish before enabling workflows that require MFA.
- [ ] **T23 · Maker-checker — R4; after T04, T06.** Identify actions requiring dual control under policy, then implement pending/approve/reject/cancel flows. Done when makers cannot approve their own requests, pending content cannot change silently and rejection causes no financial mutation. This is a prerequisite for enabling actions classified as requiring approval.
- [ ] **T24 · Define KYC/AML/sanctions scope — R5.** Record decisions about data, states, decision sources, override authority and conditions blocking account opening/transactions. Done when policy and scope are explicit before implementation; do not infer regulations or integrate providers without a defined scope. The agreed policy is an input to T13–T15.
- [ ] **T25 · Standardize errors and observability — R3.** Review handlers/API clients, stable error codes, correlation IDs and redaction. Done when tests prevent exposure of passwords/tokens/PII or the existence of out-of-scope resources, and signals cover posting failures, authorization denials and reconciliation mismatches.
- [ ] **T26 · CI and dependency evidence — R3.** Configure the chosen CI platform for backend tests, frontend lint/typecheck/build, secret scanning, dependency/license review and release SBOMs. Review the need for the milestone repository in `settings.gradle`. Done when results/artifacts are retained, dependency versions/sources are reproducible and failures are not silently ignored.
- [ ] **T27 · Backup, restore and rollback — R4.** Write runbooks and define RPO/RTO, database access, release ordering and incident recovery. Done when a restore drill succeeds with a synthetic database, ledger/balance/audit are verified after recovery, and application rollback is distinguished from forward-only migration handling.

## P3 — Extensions after requirements are agreed

- [ ] **T28 · Fees, interest and end-of-day — R4.** Define formulas, schedules, calculation basis, scale/rounding, effective dates and product versions before implementation. Done when posting, reruns, reversals and reconciliation are verified against edge cases.
- [ ] **T29 · Statements and data exports — R3.** Define reporting scope and formats; design permissions, redaction, access audit, download expiration and volume limits. Done when tests cover cross-customer access, format-specific injection and data leakage.
- [ ] **T30 · External payment integrations — R4/R5 depending on scope.** Start only after systems and data contracts are selected; design authentication, callback signatures, replay controls, timeouts, uncertain outcomes, compensation and settlement/reconciliation. Done when sandbox tests cover duplicates, out-of-order events and failures, and data flows are documented; never connect local tools to production.

## Getting started

First pass: **T01 → T02 → T03 → T04 → T05**, while designing audit in T06. Then complete **T07–T09** before building transfers. Satisfy each task's dependencies first; placement in P2 does not defer MFA, approvals or KYC decisions required by P1 workflows.

For every completed task, record the owner, commit/PR, verification commands and results. R3 and above require security impact notes and negative tests; R4 and above also require transaction, idempotency, concurrency and audit evidence, plus financial impact notes. This backlog does not attest production readiness or certified compliance.

## Planned verification commands for implementation

Run backend tests only after configuring the `test` profile to use a dedicated local PostgreSQL test database according to the [environment guide](docs/operations/environments.md). Do not use production databases or real customer data.

From the repository root:

```powershell
$env:SPRING_PROFILES_ACTIVE = 'test'
.\gradlew.bat test
```

From `customer-web`, after installing dependencies according to the lockfile:

```powershell
pnpm lint
pnpm typecheck
pnpm build
```

These commands are planned verification steps; they were not run while creating or translating this document.

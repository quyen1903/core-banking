# QuinnBank Core Coding Standard

> Entry order: `AGENTS.md` -> `SECURITY.md` -> `LIBRARY.md` -> this file ->
> `docs/`.
> This standard is mandatory for QuinnBank Core implementation and review.

---

## 1. Purpose And Bar

QuinnBank Core is a regulated banking codebase. The engineering bar is not
"working CRUD". The bar is deterministic financial behavior, explicit security,
auditable state changes, controlled data access, safe operations, and code that
senior engineers can maintain under production pressure.

The terms MUST, MUST NOT, SHOULD, SHOULD NOT, and MAY are normative.

---

## 2. Non-Negotiable Engineering Rules

- Financial correctness MUST outrank delivery speed.
- Security controls MUST be designed into the use case, not added as a final
  controller filter.
- Domain invariants MUST be enforced in domain/application code and backed by
  persistence constraints where appropriate.
- Money, balances, fees, limits, rates, settlement amounts, and reconciliation
  values MUST NOT use `double`, `float`, or binary floating point.
- Ledger records MUST be append-only unless an explicit accounting design says
  otherwise.
- Applied migrations MUST be forward-only.
- Sensitive data MUST NOT appear in logs, errors, metrics labels, test fixtures,
  screenshots, examples, or documentation.
- Cross-module access MUST use contracts, snapshots, or events.
- A failed command MUST leave the system in a known state.

---

## 3. Architecture Style

Use a modular monolith with bounded contexts until an explicit architecture
decision says otherwise.

Recommended package layout:

```text
com.quinnbank.core.<module>
  api
  application
  domain
  infrastructure
```

Dependency direction:

```text
api -> application -> domain
infrastructure -> application/domain
domain -> no web framework, no controller DTOs, no external clients
```

Layer responsibilities:

| Layer | Owns | Must not own |
| --- | --- | --- |
| `api` | HTTP routes, request/response DTOs, shallow request validation, status codes | Business decisions, transactions, repository calls |
| `application` | Use cases, commands, queries, authorization calls, transaction boundary, orchestration, event publication | Raw HTTP objects, rendering, persistence schema details |
| `domain` | Aggregates, value objects, invariants, policies, state machines, repository ports | Spring MVC, external SDKs, JSON DTOs, SQL strings |
| `infrastructure` | JPA mappings, adapters, external clients, messaging, clocks, id generators | Business policy hidden from domain/application |

For the current small project, JPA annotations on domain entities are tolerated.
As modules grow, split persistence adapters from domain models where invariants
or external contracts are becoming hard to protect.

---

## 4. Module Ownership

Each data concept has one owner.

| Concept | Owning module | Cross-module access |
| --- | --- | --- |
| Customer master data and CIF number | CIF/customer | `CustomerDirectory`, snapshots, events |
| Account product configuration | Account/product | Product snapshot/query contract |
| Bank account lifecycle | Account | Account snapshot/query contract |
| Balances and ledger postings | Ledger/accounting | Posting commands and immutable reports |
| Transfers and payments | Payment/transfer | Command API, events, reconciliation views |
| KYC, AML, sanctions, risk | Compliance | Decisions, cases, events |
| Authentication identity | Identity/access | Principal, entitlements, policy decisions |
| Audit evidence | Audit | Append-only audit API |

Do not import another module's repository just because it is in the same JVM.

---

## 5. Naming Standards

Use business names, not technical placeholders.

| Type | Pattern | Example |
| --- | --- | --- |
| Use case | Verb phrase + `UseCase` | `RegisterCustomerUseCase` |
| Command | Intent + `Command` | `OpenAccountCommand` |
| Query | Read action + `Query` | `GetAccountSummaryQuery` |
| Snapshot | Read model + `Snapshot` | `CustomerSnapshot` |
| Event | Past tense business fact | `AccountOpened` |
| Policy | Decision concept + `Policy` | `WithdrawalLimitPolicy` |
| Repository | Aggregate + `Repository` | `CustomerRepository` |
| Exception | Business failure + `Exception` | `AccountBlockedException` |

Avoid names such as `Manager`, `Util`, `Helper`, `Processor`, or `Common`
unless the responsibility is intentionally generic and cannot be expressed as a
business concept.

---

## 6. Domain Model Rules

Aggregates MUST protect valid state.

Rules:

- Constructors and factories MUST produce valid objects.
- State changes MUST use named methods such as `block()`, `close()`,
  `postDebit()`, or `markKycVerified()`.
- Public setters MUST NOT exist on aggregates unless the field has no invariant
  impact.
- Domain methods MUST reject invalid transitions.
- Domain rules MUST not depend on HTTP request DTOs.
- Time-sensitive behavior SHOULD use injected `Clock`.
- Domain events SHOULD be emitted as business facts after successful state
  transitions.
- Value objects SHOULD be immutable and validate themselves.
- Entity equality MUST be deliberate and tested when used in collections.

Bad:

```java
customer.setStatus(CustomerStatus.ACTIVE);
account.setAvailableBalance(account.getAvailableBalance().subtract(amount));
```

Good:

```java
customer.activate(approval);
account.reserveWithdrawal(amount, commandId, clock.instant());
```

---

## 7. Money, Currency, Rates, And Rounding

Money handling is high-risk.

Mandatory rules:

- Use `BigDecimal` or a reviewed money value object.
- Database monetary columns MUST define precision and scale explicitly.
- Currency MUST be explicit using ISO 4217 code or a reviewed currency value
  object.
- Rounding mode MUST be explicit at every calculation boundary.
- Never compare `BigDecimal` values with `equals()` when scale is irrelevant.
  Use `compareTo()` or a money value object.
- Do not mix currencies in one arithmetic operation.
- Persist calculated amounts needed for audit and historical display.
- Rate calculations MUST define day-count basis, compounding behavior, scale,
  rounding, and effective date.

Example:

```java
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN);
    }
}
```

Do not introduce a money abstraction casually. Introduce it when it centralizes
currency, rounding, and validation rules for real workflows.

---

## 8. Ledger And Accounting Rules

Ledger code has the highest correctness bar.

Ledger entries SHOULD be immutable. Corrections SHOULD be posted as reversals or
adjustments, never silent edits.

A posting workflow MUST define:

- Journal id.
- Debit and credit accounts.
- Amount and currency.
- Posting date.
- Value date.
- Source command id.
- Idempotency key or unique provider event id.
- Actor or system origin.
- Business reason.
- Audit correlation id.

Balanced posting invariant:

```text
sum(debits by currency) == sum(credits by currency)
```

Posting state transitions MUST be explicit:

```text
RECEIVED -> VALIDATED -> POSTED
RECEIVED -> REJECTED
POSTED -> REVERSED
```

Do not update balances independently from the ledger design. If cached balances
exist, define how they reconcile to the immutable ledger.

---

## 9. Transaction And Concurrency Rules

Application use cases own transaction boundaries.

Rules:

- Use `@Transactional` at the use-case/application service boundary.
- Keep transactions short.
- Do not perform slow external calls inside database transactions unless the
  failure semantics are explicitly designed.
- Use optimistic locking for mutable aggregates where concurrent updates are
  expected.
- Use unique constraints for idempotency keys and external event ids.
- Use atomic database updates or locks for stock-like counters and balances.
- Retry only when the operation is safe to repeat.
- Duplicate commands MUST produce the same business result or a deterministic
  duplicate response.

Bad:

```java
account.setBalance(account.getBalance().subtract(amount));
accountRepository.save(account);
paymentClient.notifyTransferPosted(...);
```

Good shape:

```text
1. Validate command and authorization.
2. Load aggregate with version or lock.
3. Apply domain transition.
4. Persist state and outbox event in same transaction.
5. Publish asynchronously from outbox.
```

---

## 10. Idempotency Standard

External commands that can be retried MUST be idempotent when they create or
mutate financial state.

Examples:

- Open account.
- Create transfer.
- Post ledger entry.
- Apply fee.
- Process payment webhook.
- Refund.
- Reverse transaction.
- Import batch file.

Required fields:

- Idempotency key or external event id.
- Actor or source system.
- Request fingerprint where appropriate.
- First result status.
- Replay behavior.
- Expiration/retention rule.

If a replay has the same key but different command fingerprint, reject it as a
conflict.

---

## 11. API Standard

Controllers are adapters, not business services.

Rules:

- Public APIs MUST be versioned, for example `/api/v1/...`.
- Controllers MUST map request DTOs to commands or queries.
- Controllers MUST NOT call repositories directly.
- Request DTO validation handles shape, size, type, and basic format.
- Business validation lives in application/domain layers.
- Responses MUST not expose JPA entities directly.
- Sensitive fields MUST be omitted or masked.
- Pagination MUST have bounded maximums.
- Sorting MUST use allowlisted fields.
- Idempotent command endpoints SHOULD accept an idempotency key header.
- Error responses MUST use stable error codes and safe messages.

Preferred error shape:

```json
{
  "code": "ACCOUNT_BLOCKED",
  "message": "The account cannot perform this operation.",
  "correlationId": "01HX..."
}
```

Do not return stack traces, SQL names, hostnames, secrets, policy internals, or
existence hints for protected resources.

---

## 12. Authorization In Application Code

Authorization MUST answer:

1. Who is the actor?
2. What action is requested?
3. Which resource is targeted?
4. What contextual restrictions apply?

Context may include:

- Customer relationship.
- Account ownership.
- Branch or tenant.
- Channel.
- Device or network risk.
- Transaction amount.
- KYC/AML status.
- Maker-checker state.
- Time window.

Do not trust client-supplied `customerId`, `accountId`, role, branch, or
permission values. Resolve identity and entitlements from authenticated context.

---

## 13. Persistence And Flyway Standard

Migrations are production artifacts.

Rules:

- Use lower_snake_case for table and column names.
- Use plural table names consistently unless a module has an explicit convention.
- Every table SHOULD have a primary key.
- Mutable business tables SHOULD have `created_at`, `updated_at`, and `version`.
- Audit/event tables SHOULD be append-only and may omit `updated_at`.
- Monetary columns MUST use explicit `NUMERIC(p, s)`.
- Status columns MUST be backed by explicit application state machines.
- Foreign keys MUST be explicit for owned relationships.
- Indexes MUST match query predicates.
- Large backfills MUST be split from schema changes when needed.
- Do not use `ddl-auto` to mutate shared databases.

Migration review checklist:

- Entity mapping agrees with migration.
- Nullability is intentional.
- Defaults are safe for existing rows.
- Unique constraints are safe with existing data.
- Indexes do not create unacceptable write overhead.
- Locks and table rewrites are understood.
- Roll-forward mitigation is documented.

---

## 14. Validation And Exception Standard

Validation layers:

| Layer | Validates |
| --- | --- |
| API | Shape, type, length, format, enum values |
| Application | Authorization, use-case preconditions, duplicate command |
| Domain | Invariants and state transitions |
| Database | Uniqueness, foreign keys, non-null, precision |

Exception rules:

- Throw domain-specific exceptions for expected business failures.
- Do not leak persistence exceptions as API messages.
- Do not catch broad `Exception` and continue.
- Do not convert security denial into business success.
- Map exceptions centrally where practical.
- Include correlation id in external error responses.

---

## 15. Security Coding Standard

All code must satisfy `SECURITY.md`.

Implementation rules:

- Authenticate every non-public route.
- Enforce authorization before sensitive reads or mutations.
- Use server-side resource scoping in query predicates.
- Mask sensitive fields in logs and responses.
- Validate all external input, including queue messages and webhooks.
- Protect against mass assignment by mapping explicit fields.
- Never log request bodies by default.
- Never store plaintext secrets or tokens.
- Use approved libraries for cryptography.
- Add negative tests for authorization changes.

---

## 16. Library Governance Standard

Dependency and library decisions MUST follow `LIBRARY.md`.

Do not add or upgrade libraries, Gradle plugins, annotation processors,
generated-code tools, runtime drivers, container images, or CI tools without
considering supply-chain, license, vulnerability, telemetry, and operational
risk.

---

## 17. Integration And Messaging Standard

External systems are unreliable and untrusted until verified.

Rules:

- Use typed clients or adapters under `infrastructure`.
- Validate all inbound integration payloads.
- Verify webhook signatures where applicable.
- Store external event ids for idempotency.
- Use timeouts, retries, circuit breakers, and dead-letter handling where
  operationally required.
- Do not assume message delivery is exactly once.
- Use outbox for events that must be published after a database transaction.
- Message payloads MUST not contain secrets and SHOULD minimize sensitive data.

---

## 18. Logging, Audit, And Observability

Application logs:

- Structured where practical.
- Include correlation id.
- Include module, use case, and stable business status where safe.
- Exclude secrets and sensitive payloads.

Audit records:

- Are evidence, not diagnostics.
- Must be append-only or tamper-evident.
- Must include actor, action, entity, timestamp, decision, correlation id, and
  safe old/new values when required.

Metrics SHOULD cover:

- API latency and error rate.
- Authentication and authorization failures.
- Financial command acceptance/rejection.
- Posting failures.
- Duplicate command rate.
- Outbox lag.
- Reconciliation exceptions.
- Database lock/conflict rate.

---

## 19. Testing Standard

Tests must scale with risk.

| Risk area | Minimum tests |
| --- | --- |
| Domain state transition | Unit tests for valid and invalid transitions |
| Application use case | Transaction, duplicate command, authorization behavior |
| API endpoint | Validation, status code, response contract, safe errors |
| Repository/migration | Mapping and constraint validation |
| Security | Positive and negative access tests |
| Financial workflow | Idempotency, concurrency, reversal/failure path |
| Integration callback | Signature validation and replay handling |

Test data MUST be synthetic. Do not copy real names, account numbers, phone
numbers, emails, cards, addresses, KYC documents, or transaction histories.

Financial tests SHOULD include:

- Boundary amounts.
- Zero and negative amount rejection.
- Currency mismatch.
- Duplicate command.
- Concurrent update.
- Rounding edge cases.
- Reversal path.

---

## 20. Code Review Standard

Reviewers MUST block changes that:

- Mutate financial state without transaction and idempotency design.
- Use unsafe money types.
- Expose sensitive data.
- Add broad authorization bypasses.
- Return JPA entities from APIs.
- Bypass domain state transitions.
- Modify applied migrations.
- Add external dependencies without justification.
- Lack tests around high-risk behavior.
- Hide failures with generic exception handling.

Reviewers SHOULD ask:

- What invariant does this protect?
- What happens on retry?
- What happens under concurrent requests?
- What is audited?
- What is masked?
- What breaks if the external system is down?
- How is this rolled back or rolled forward?

---

## 21. Definition Of Done

A change is done when:

- Correct module owns the behavior.
- Business rules are explicit.
- Security impact is considered.
- Financial integrity impact is considered.
- Relevant tests pass.
- Migration impact is reviewed.
- Logs and errors are safe.
- Audit requirements are satisfied.
- Documentation is updated for changed contracts or operations.
- Residual risks are documented.

---

## 22. Current Repo Notes

The current codebase is early-stage. Until fixed, reviewers must pay attention
to:

- Entity and migration table-name consistency.
- Timestamp column-name consistency.
- Removal of web annotations from application services.
- Introduction of module contracts before account/ledger modules depend on CIF.
- Addition of security configuration and authorization tests before exposing
  sensitive endpoints.

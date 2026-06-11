# CIF Module Design

## Naming

This document treats the requested CFI module as the banking CIF module:
Customer Information File. The current code already hints at this with
`customerNumber = "CIF" + ...`.

If CFI is meant to mean another domain, keep the same modular shape but rename
the bounded context and its public contracts.

## Goal

CIF owns the bank customer master record. Other modules can reference a customer
by id or CIF number, but they should not own customer profile, lifecycle, KYC,
or contact data.

Primary responsibilities:

- Register a customer.
- Maintain identity/profile/contact data.
- Track customer lifecycle status.
- Track KYC state and risk profile when that feature arrives.
- Expose read-only customer snapshots to account, card, loan, and transaction
  modules.
- Publish domain events when customer state changes.

Out of scope:

- Account balances and account status.
- Transactions and ledger posting.
- Loan underwriting.
- Authentication user accounts.
- Notification delivery.

## Module Boundary

Recommended package:

```text
com.quinnbank.core.cif
  api
  application
  domain
  infrastructure
```

Layer rules:

```text
api            -> application
application    -> domain
infrastructure -> application, domain
domain         -> no Spring, no persistence framework dependencies where practical
```

In the current small Spring Boot app, using JPA annotations directly on the
domain entity is acceptable. When CIF becomes more complex, split persistence
models into `infrastructure.persistence`.

## Public Contract

Other modules should depend on a small application-facing contract, not the JPA
entity:

```java
public interface CustomerDirectory {
    CustomerSnapshot requireActiveCustomer(UUID customerId);
    Optional<CustomerSnapshot> findByCustomerNumber(String customerNumber);
}
```

```java
public record CustomerSnapshot(
        UUID id,
        String customerNumber,
        String fullName,
        CustomerStatus status
) {
}
```

This keeps account creation simple:

```text
Account module asks CIF: requireActiveCustomer(customerId)
CIF returns CustomerSnapshot
Account stores customerId only
```

## Use Cases

Initial use cases:

- `RegisterCustomerUseCase`
- `GetCustomerProfileQuery`
- `SearchCustomersQuery`
- `UpdateCustomerProfileUseCase`
- `ChangeCustomerStatusUseCase`

Future use cases:

- `StartKycReviewUseCase`
- `ApproveKycUseCase`
- `RejectKycUseCase`
- `AttachCustomerDocumentUseCase`
- `MergeDuplicateCustomersUseCase`

## Domain Model

Core aggregate:

```text
Customer
  id
  customerNumber
  fullName
  email
  phone
  status
  kycStatus
  riskRating
  createdAt
  updatedAt
```

Enums:

```text
CustomerStatus: ACTIVE, INACTIVE, BLOCKED, CLOSED
KycStatus: NOT_STARTED, PENDING_REVIEW, VERIFIED, REJECTED, EXPIRED
RiskRating: LOW, MEDIUM, HIGH
```

Suggested invariants:

- Customer id is generated inside CIF.
- Customer number is unique and immutable.
- Email is normalized before uniqueness checks.
- A blocked or closed customer cannot open new accounts.
- KYC state changes are explicit use cases, not raw field updates.

## API Shape

REST endpoints:

```text
POST   /api/v1/customers
GET    /api/v1/customers/{id}
GET    /api/v1/customers?query=&status=&page=&size=
PATCH  /api/v1/customers/{id}/profile
PATCH  /api/v1/customers/{id}/status
```

Request/response DTOs stay in `api`; commands and query objects stay in
`application`.

## Persistence

Tables:

```text
customers
customer_contacts       -- optional when one customer has multiple contacts
customer_documents      -- optional for KYC/document uploads
customer_audit_events   -- optional immutable business audit trail
```

Minimum `customers` table:

```sql
CREATE TABLE customers (
    id UUID PRIMARY KEY,
    customer_number VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    kyc_status VARCHAR(50) NOT NULL,
    risk_rating VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

Implementation note:

- CIF is implemented under `com.quinnbank.core.cif`.
- `V2__align_cif_customer_schema.sql` aligns the existing `customers` table
  forward-only by renaming timestamp columns, widening `full_name`, and adding
  CIF lifecycle fields.
- The public API path remains `/api/v1/customers`.

## Events

Internal domain events:

```text
CustomerRegistered
CustomerProfileUpdated
CustomerStatusChanged
KycStatusChanged
```

In a modular monolith, start with Spring application events. If the app later
splits into services, map these to an outbox table and publish to a broker.

## Refactor Path From Current Code

Step 1: Stabilize current customer module. Completed by CIF implementation.

- Fix table name and timestamp column mismatch.
- Remove web annotations from `CustomerService`.
- Add `CustomerDirectory` and `CustomerSnapshot` for cross-module reads.

Step 2: Rename conceptually to CIF without a large package move. Superseded by
explicit `cif` package implementation.

- Keep package `customer` if you want minimal churn.
- Rename service/use cases and docs to CIF language.
- Keep API path `/api/v1/customers` because it is user-facing and clear.

Step 3: Move to explicit module package when the domain grows. Started.

- Move `customer` to `cif`.
- Split commands, queries, use cases, ports, and persistence adapters.
- Add module tests around invariants and contract behavior.

## Recommended First Implementation Slice

Small, useful slice:

```text
cif/api/CustomerController
cif/application/RegisterCustomerUseCase
cif/application/GetCustomerProfileQuery
cif/application/CustomerDirectory
cif/application/CustomerSnapshot
cif/domain/Customer
cif/domain/CustomerRepository
cif/domain/CustomerNumberGenerator
cif/domain/CustomerStatus
cif/domain/KycStatus
cif/infrastructure/SystemClockCustomerNumberGenerator
```

This gives the project a clean modular boundary without forcing microservices
or heavy abstractions too early.

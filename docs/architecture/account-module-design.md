# Account Module Design

## Scope

Account owns account product configuration and bank account lifecycle. It does
not own CIF customer profile data, ledger postings, transfers, fees, interest
accrual, reconciliation, KYC, AML, sanctions, or authentication.

Initial production slice:

- open a bank account for an active CIF customer;
- store account product and customer references by id;
- use explicit currency and `NUMERIC(19, 4)` balances;
- keep account opening transactional and idempotent;
- reject zero-balance opening when the selected product requires funding.

## Package Shape

```text
account
├── api
│   ├── command
│   │   └── AccountCommandController.java
│   ├── query
│   │   └── AccountQueryController.java
│   ├── dto
│   │   ├── OpenAccountRequest.java
│   │   └── AccountResponse.java
│   └── mapper
│       └── AccountHttpMapper.java
├── application
│   ├── command
│   │   └── OpenAccountCommand.java
│   ├── query
│   │   └── GetAccountByIdQuery.java
│   ├── port
│   │   ├── in
│   │   │   ├── OpenAccountUseCase.java
│   │   │   └── GetAccountUseCase.java
│   │   └── out
│   │       ├── AccountRepositoryPort.java
│   │       ├── CustomerLookupPort.java
│   │       ├── AccountProductLookupPort.java
│   │       ├── AccountNumberGeneratorPort.java
│   │       └── IdempotencyPort.java
│   ├── result
│   │   ├── AccountSnapshot.java
│   │   └── AccountOpeningIdempotencyResult.java
│   └── service
│       ├── OpenAccountService.java
│       └── GetAccountService.java
├── domain
│   ├── model
│   │   ├── BankAccount.java
│   │   ├── AccountProduct.java
│   │   ├── AccountNumber.java
│   │   ├── AccountStatus.java
│   │   └── Money.java
│   ├── policy
│   │   └── AccountOpeningPolicy.java
│   ├── event
│   │   └── AccountOpenedEvent.java
│   └── exception
│       └── AccountOpeningRejectedException.java
└── infrastructure
    ├── persistence
    │   ├── BankAccountJpaEntity.java
    │   ├── AccountProductJpaEntity.java
    │   ├── SpringDataBankAccountRepository.java
    │   ├── SpringDataAccountProductRepository.java
    │   ├── BankAccountPersistenceAdapter.java
    │   ├── AccountProductPersistenceAdapter.java
    │   ├── BankAccountPersistenceMapper.java
    │   └── AccountProductPersistenceMapper.java
    ├── customer
    │   └── CifCustomerLookupAdapter.java
    └── generator
        └── AccountNumberGeneratorAdapter.java
```

## Command Flow

```text
AccountCommandController
  -> AccountHttpMapper
  -> OpenAccountUseCase
  -> OpenAccountService
  -> IdempotencyPort
  -> CustomerLookupPort
  -> AccountProductLookupPort
  -> AccountOpeningPolicy
  -> BankAccount.open(...)
  -> AccountRepositoryPort
```

## Financial Integrity Notes

Opening an account creates no ledger entry and does not mutate customer state.
Balances start at zero and are not client supplied.

Any funded opening, deposit, withdrawal, fee, interest, transfer, reversal, or
balance projection must be implemented through a ledger/accounting design before
balances are changed.

`bank_accounts.version` provides optimistic locking for later lifecycle
transitions. `opening_idempotency_key` is unique and
`opening_request_fingerprint` makes retries deterministic.

## Security Notes

HTTP account opening requires `ACCOUNT_OPEN`. Account reads require
`ACCOUNT_VIEW`. The API response returns a masked account number.

Current authorization is still authority-level only. Customer/account ownership,
branch scope, signatory rights, and maker-checker are required before this is
ready for customer-facing or privileged production workflows.

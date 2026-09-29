# core-banking

src/
├── main/
│   ├── java/com/quinnbank/core/        #Entire project is built based on SQRS pattern
│   │   ├── QuinnBankApplication.java
│   │   │
│   │   ├── customer/                         # Module Business
│   │   │   ├── domain/            #Architech, Rules and State of business
│   │   │   │   ├── model/
│   │   │   │   │   └── Customer.java
│   │   │   │   ├── valueobject/
│   │   │   │   │   └── CustomerId.java
│   │   │   │   ├── policy/
│   │   │   │   │   └── CustomerActivationPolicy.java
│   │   │   │   └── exception/
│   │   │   │       └── CustomerRegistrationRejectedException.java
│   │   │   │
│   │   │   ├── application/        #Coordinate an use case, define port
│   │   │   │   ├── port/
│   │   │   │   │   ├── in/     #in-bound from the outside: HTTP, CLI, message customer, etc
│   │   │   │   │   │   ├── RegisterCustomerUseCase.java
│   │   │   │   │   │   └── GetCustomerByIdUseCase.java
│   │   │   │   │   └── out/    #Perform application requirement: Database operation, API calls, message sending etc
│   │   │   │   │       ├── CustomerWritePort.java
│   │   │   │   │       ├── CustomerReadPort.java
│   │   │   │   │       └── CustomerNumberGeneratorPort.java
│   │   │   │   ├── command/
│   │   │   │   │   └── RegisterCustomerCommand.java
│   │   │   │   ├── query/
│   │   │   │   │   └── GetCustomerByIdQuery.java
│   │   │   │   ├── result/
│   │   │   │   │   ├── RegisterCustomerResult.java
│   │   │   │   │   └── CustomerDetails.java
│   │   │   │   └── service/
│   │   │   │       ├── RegisterCustomerService.java
│   │   │   │       └── GetCustomerByIdService.java
│   │   │   │
│   │   │   ├── adapter/
│   │   │   │   ├── in/
│   │   │   │   │   └── web/
│   │   │   │   │       ├── CustomerController.java
│   │   │   │   │       ├── CustomerExceptionHandler.java
│   │   │   │   │       ├── dto/
│   │   │   │   │       │   ├── RegisterCustomerRequest.java
│   │   │   │   │       │   └── RegisterCustomerResponse.java
│   │   │   │   │       └── mapper/
│   │   │   │   │           └── CustomerWebMapper.java
│   │   │   │   └── out/
│   │   │   │       ├── persistence/
│   │   │   │       │   ├── CustomerWritePersistenceAdapter.java
│   │   │   │       │   ├── CustomerReadPersistenceAdapter.java
│   │   │   │       │   ├── CustomerJpaEntity.java
│   │   │   │       │   ├── CustomerJpaRepository.java
│   │   │   │       │   └── CustomerPersistenceMapper.java
│   │   │   │       └── generator/
│   │   │   │           └── DefaultCustomerNumberGenerator.java
│   │   │   │
│   │   │   └── configuration/      #Initialize and connect service with Spring 
│   │   │       ├── CustomerConfiguration.java
│   │   │       └── TransactionalRegisterCustomerUseCase.java
│   │   │
│   │   ├── account/         
│   │   └── ledger/
│   │
│   └── resources/
│       ├── application.yml
│       └── db/migration/
│
└── test/java/com/quinnbank/core/customer/
    ├── domain/
    ├── application/
    ├── adapter/
    │   ├── in/web/
    │   └── out/persistence/
    └── architecture/


The direction of dependency
Web Controller ──────────────> Input Port
Application Service ─────────> Input Port       (implements)
Application Service ─────────> Domain
Application Service ─────────> Output Port
Persistence Adapter ─────────> Output Port      (implements)
Persistence Adapter ─────────> JPA Repository
Configuration ───────────────> Service + Adapter để nối chúng
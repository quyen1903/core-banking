CREATE TABLE customers
(
    id              UUID PRIMARY KEY,
    customer_number VARCHAR(50) NOT NULL UNIQUE,
    full_name       VARCHAR(50) NOT NULL,
    email           VARCHAR(255) UNIQUE,
    phone           VARCHAR(50),
    status          VARCHAR(50) NOT NULL,
    create_at       TIMESTAMP   NOT NULL,
    update_at       TIMESTAMP   NOT NULL
);

CREATE TABLE account_products
(
    id            UUID PRIMARY KEY,
    code          VARCHAR(50)    NOT NULL UNIQUE,
    name          VARCHAR(255)   NOT NULL,
    currency      VARCHAR(3)     NOT NULL,
    min_balance   NUMERIC(19, 4) NOT NULL DEFAULT 0,
    interest_rate NUMERIC(9, 6)  NOT NULL DEFAULT 0,
    monthly_fee   NUMERIC(19, 4) NOT NULL DEFAULT 0,
    active        BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP      NOT NULL,
    updated_at    TIMESTAMP      NOT NULL
);

CREATE TABLE bank_accounts
(
    id                UUID PRIMARY KEY,
    account_number    VARCHAR(50)    NOT NULL UNIQUE,
    customer_id       UUID           NOT NULL REFERENCES customers (id),
    product_id        UUID           NOT NULL REFERENCES account_products (id),
    currency          VARCHAR(3)     NOT NULL,
    available_balance NUMERIC(19, 4) NOT NULL DEFAULT 0,
    current_balance   NUMERIC(19, 4) NOT NULL DEFAULT 0,
    status            VARCHAR(50)    NOT NULL,
    version           BIGINT         NOT NULL DEFAULT 0,
    created_at        TIMESTAMP      NOT NULL,
    updated_at        TIMESTAMP      NOT NULL
);
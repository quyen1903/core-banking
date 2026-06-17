CREATE TABLE ledger_journals
(
    id                  UUID PRIMARY KEY,
    source_command_id   VARCHAR(120) NOT NULL,
    idempotency_key     VARCHAR(120) NOT NULL,
    command_fingerprint TEXT         NOT NULL,
    actor_type          VARCHAR(50)  NOT NULL,
    actor_id            VARCHAR(120) NOT NULL,
    business_reason     VARCHAR(255) NOT NULL,
    posting_date        DATE         NOT NULL,
    value_date          DATE         NOT NULL,
    currency            VARCHAR(3)   NOT NULL,
    status              VARCHAR(50)  NOT NULL,
    correlation_id      VARCHAR(120) NOT NULL,
    created_at          TIMESTAMP    NOT NULL,
    posted_at           TIMESTAMP    NOT NULL,
    version             BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT uq_ledger_journals_idempotency_key UNIQUE (idempotency_key)
);

CREATE INDEX ix_ledger_journals_source_command_id ON ledger_journals (source_command_id);
CREATE INDEX ix_ledger_journals_posting_date ON ledger_journals (posting_date);

CREATE TABLE ledger_entries
(
    id          UUID PRIMARY KEY,
    journal_id  UUID           NOT NULL REFERENCES ledger_journals (id),
    line_number INTEGER        NOT NULL,
    account_id  UUID           NOT NULL REFERENCES bank_accounts (id),
    entry_side  VARCHAR(10)    NOT NULL,
    currency    VARCHAR(3)     NOT NULL,
    amount      NUMERIC(19, 4) NOT NULL,
    created_at  TIMESTAMP      NOT NULL,
    CONSTRAINT uq_ledger_entries_journal_line UNIQUE (journal_id, line_number),
    CONSTRAINT ck_ledger_entries_amount_positive CHECK (amount > 0),
    CONSTRAINT ck_ledger_entries_side CHECK (entry_side IN ('DEBIT', 'CREDIT'))
);

CREATE INDEX ix_ledger_entries_account_id ON ledger_entries (account_id);
CREATE INDEX ix_ledger_entries_journal_id ON ledger_entries (journal_id);

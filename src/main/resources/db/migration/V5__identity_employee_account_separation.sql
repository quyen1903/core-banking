ALTER TABLE bank_accounts
    ADD COLUMN opened_at TIMESTAMP,
    ADD COLUMN closed_at TIMESTAMP;

UPDATE bank_accounts
SET opened_at = created_at
WHERE opened_at IS NULL;

ALTER TABLE bank_accounts
    ALTER COLUMN opened_at SET NOT NULL;

CREATE TABLE identity_accounts
(
    id           UUID PRIMARY KEY,
    username     VARCHAR(120) NOT NULL UNIQUE,
    email        VARCHAR(255) UNIQUE,
    subject_type VARCHAR(50)  NOT NULL,
    subject_id   UUID,
    status       VARCHAR(50)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    version      BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT ck_identity_accounts_subject_type
        CHECK (subject_type IN ('EMPLOYEE', 'CUSTOMER', 'SERVICE_ACCOUNT', 'SYSTEM')),
    CONSTRAINT ck_identity_accounts_subject_required
        CHECK (
            (subject_type IN ('EMPLOYEE', 'CUSTOMER') AND subject_id IS NOT NULL)
            OR (subject_type IN ('SERVICE_ACCOUNT', 'SYSTEM') AND subject_id IS NULL)
        )
);

CREATE INDEX ix_identity_accounts_subject ON identity_accounts (subject_type, subject_id);

CREATE TABLE credentials
(
    id                  UUID PRIMARY KEY,
    identity_account_id UUID         NOT NULL REFERENCES identity_accounts (id),
    credential_type     VARCHAR(50)  NOT NULL,
    password_hash       VARCHAR(255) NOT NULL,
    active              BOOLEAN      NOT NULL,
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP    NOT NULL,
    version             BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT uq_credentials_identity_account UNIQUE (identity_account_id),
    CONSTRAINT ck_credentials_type CHECK (credential_type IN ('PASSWORD'))
);

CREATE TABLE roles
(
    id          UUID PRIMARY KEY,
    code        VARCHAR(80)  NOT NULL UNIQUE,
    name        VARCHAR(120) NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    version     BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE permissions
(
    id          UUID PRIMARY KEY,
    code        VARCHAR(120) NOT NULL UNIQUE,
    name        VARCHAR(120) NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    version     BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE role_permissions
(
    role_id       UUID NOT NULL REFERENCES roles (id),
    permission_id UUID NOT NULL REFERENCES permissions (id),
    created_at    TIMESTAMP NOT NULL,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE identity_account_roles
(
    identity_account_id UUID      NOT NULL REFERENCES identity_accounts (id),
    role_id             UUID      NOT NULL REFERENCES roles (id),
    assigned_at         TIMESTAMP NOT NULL,
    PRIMARY KEY (identity_account_id, role_id)
);

CREATE TABLE employee
(
    id                  UUID PRIMARY KEY,
    employee_number     VARCHAR(50) NOT NULL UNIQUE,
    identity_account_id UUID REFERENCES identity_accounts (id),
    status              VARCHAR(50) NOT NULL,
    created_at          TIMESTAMP   NOT NULL,
    updated_at          TIMESTAMP   NOT NULL,
    version             BIGINT      NOT NULL DEFAULT 0,
    CONSTRAINT uq_employee_identity_account UNIQUE (identity_account_id),
    CONSTRAINT ck_employee_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'TERMINATED'))
);

CREATE INDEX ix_employee_identity_account_id ON employee (identity_account_id);

CREATE TABLE employee_profiles
(
    employee_id UUID PRIMARY KEY REFERENCES employee (id),
    full_name   VARCHAR(255) NOT NULL,
    work_email  VARCHAR(255) NOT NULL UNIQUE,
    job_title   VARCHAR(120)
);

CREATE TABLE employee_branch_assignments
(
    id                 UUID PRIMARY KEY,
    employee_id        UUID        NOT NULL REFERENCES employee (id),
    branch_code        VARCHAR(50) NOT NULL,
    primary_assignment BOOLEAN     NOT NULL,
    active             BOOLEAN     NOT NULL,
    assigned_at        TIMESTAMP   NOT NULL
);

CREATE INDEX ix_employee_branch_assignments_employee_id ON employee_branch_assignments (employee_id);
CREATE INDEX ix_employee_branch_assignments_branch_code ON employee_branch_assignments (branch_code);
CREATE UNIQUE INDEX uq_employee_primary_active_branch_assignment
    ON employee_branch_assignments (employee_id)
    WHERE active AND primary_assignment;

ALTER TABLE customers
    RENAME COLUMN create_at TO created_at;

ALTER TABLE customers
    RENAME COLUMN update_at TO updated_at;

ALTER TABLE customers
    ALTER COLUMN full_name TYPE VARCHAR(255);

ALTER TABLE customers
    ADD COLUMN kyc_status VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED',
    ADD COLUMN risk_rating VARCHAR(50) NOT NULL DEFAULT 'LOW',
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

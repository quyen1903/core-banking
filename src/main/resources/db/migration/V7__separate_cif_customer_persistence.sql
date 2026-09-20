-- Preserve legacy full names; splitting names requires an explicit data remediation process.
-- Apply before deploying the separated CIF persistence adapter.
ALTER TABLE customers
    ALTER COLUMN full_name TYPE VARCHAR(511),
    ADD COLUMN first_name VARCHAR(255),
    ADD COLUMN last_name VARCHAR(255),
    ADD COLUMN office_id VARCHAR(50),
    ADD COLUMN external_id VARCHAR(50);

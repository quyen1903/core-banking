ALTER TABLE bank_accounts
    ADD COLUMN opening_idempotency_key VARCHAR(120),
    ADD COLUMN opening_request_fingerprint VARCHAR(255);

UPDATE bank_accounts
SET opening_idempotency_key = 'legacy-' || id,
    opening_request_fingerprint = 'legacy|' || customer_id || '|' || product_id
WHERE opening_idempotency_key IS NULL;

ALTER TABLE bank_accounts
    ALTER COLUMN opening_idempotency_key SET NOT NULL,
    ALTER COLUMN opening_request_fingerprint SET NOT NULL;

ALTER TABLE bank_accounts
    ADD CONSTRAINT uq_bank_accounts_opening_idempotency_key UNIQUE (opening_idempotency_key);

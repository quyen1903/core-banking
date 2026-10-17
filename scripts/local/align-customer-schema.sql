-- Local development only: reconcile the existing core_banking database with
-- the current CustomerJpaEntity and Customer registration contract.
-- Hibernate update does not remove NOT NULL from obsolete/optional columns.
-- full_name is retained as historical data; the API derives its display name
-- from first_name and last_name. external_id is optional in the domain.
-- This script does not delete rows, backfill names, or change financial tables.
-- Run with psql -X -v ON_ERROR_STOP=1 against the local core_banking database.

BEGIN;
SET LOCAL lock_timeout = '5s';
SET LOCAL statement_timeout = '30s';

DO $$
BEGIN
    IF current_database() <> 'core_banking'
        OR inet_server_addr() IS NULL
        OR inet_server_addr() NOT IN ('127.0.0.1'::inet, '::1'::inet) THEN
        RAISE EXCEPTION 'This script is only for the loopback core_banking development database';
    END IF;

    -- Fresh databases created from the current entity have no full_name column.
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'customers'
            AND column_name = 'full_name'
    ) THEN
        ALTER TABLE public.customers ALTER COLUMN full_name DROP NOT NULL;
    END IF;

    ALTER TABLE public.customers ALTER COLUMN external_id DROP NOT NULL;
END $$;

COMMIT;

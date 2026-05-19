-- Drop check and unique constraints on priority_order if they still exist.
-- These were already removed manually on some environments; IF EXISTS makes this idempotent.
ALTER TABLE mentee_applications
    DROP CONSTRAINT IF EXISTS mentee_applications_priority_order_check;

ALTER TABLE mentee_applications
    DROP CONSTRAINT IF EXISTS unique_mentee_cycle_priority;

-- Normalize existing NULL priority_order values to 0 to align with the column default set in V36.
UPDATE mentee_applications
    SET priority_order = 0
    WHERE priority_order IS NULL;

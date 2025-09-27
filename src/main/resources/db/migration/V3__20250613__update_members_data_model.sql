-- Update the MEMBERS table to remove permission_id and set email as NOT NULL
-- Make it compatible with both PostgreSQL and H2 (used on Fly.io)
ALTER TABLE members DROP COLUMN IF EXISTS permission_id;
ALTER TABLE members ALTER COLUMN email SET NOT NULL;
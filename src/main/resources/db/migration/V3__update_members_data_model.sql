-- Update the MEMBERS table to remove permission_id and set email as NOT NULL
ALTER TABLE MEMBERS
    DROP COLUMN permission_id,
    ALTER COLUMN email SET NOT NULL;
-- V22: Refactor CycleStatus from PostgreSQL ENUM to table-based approach
-- Purpose: Follow MemberType pattern with integer IDs for consistency and flexibility
-- Related: CycleStatus Refactoring Plan (docs/cycle-status-refactoring-plan.md)

-- ============================================================================
-- 1. CREATE cycle_statuses REFERENCE TABLE
-- ============================================================================

CREATE TABLE IF NOT EXISTS cycle_statuses (
    id          INTEGER PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Seed with existing status values (maintaining same order as enum)
INSERT INTO cycle_statuses (id, name, description) VALUES
    (1, 'draft', 'Cycle created but not yet open for registration'),
    (2, 'open', 'Registration is currently open'),
    (3, 'closed', 'Registration has closed'),
    (4, 'in_progress', 'Cycle is active, mentorship ongoing'),
    (5, 'completed', 'Cycle has finished successfully'),
    (6, 'cancelled', 'Cycle was cancelled');

-- ============================================================================
-- 2. ADD NEW INTEGER COLUMN TO mentorship_cycles
-- ============================================================================

-- Add new integer column (nullable initially for data migration)
ALTER TABLE mentorship_cycles
ADD COLUMN cycle_status_id INTEGER;

-- Add foreign key constraint
ALTER TABLE mentorship_cycles
ADD CONSTRAINT fk_mentorship_cycles_status
    FOREIGN KEY (cycle_status_id)
    REFERENCES cycle_statuses(id)
    ON DELETE RESTRICT;

-- ============================================================================
-- 3. MIGRATE EXISTING DATA
-- ============================================================================

-- Map existing ENUM values to integer IDs
UPDATE mentorship_cycles
SET cycle_status_id = CASE status::text
    WHEN 'draft' THEN 1
    WHEN 'open' THEN 2
    WHEN 'closed' THEN 3
    WHEN 'in_progress' THEN 4
    WHEN 'completed' THEN 5
    WHEN 'cancelled' THEN 6
END;

-- Verify all rows have been migrated
DO $$
DECLARE
    null_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO null_count
    FROM mentorship_cycles
    WHERE cycle_status_id IS NULL;

    IF null_count > 0 THEN
        RAISE EXCEPTION 'Migration failed: % rows have NULL cycle_status_id', null_count;
    END IF;
END $$;

-- ============================================================================
-- 4. MAKE NEW COLUMN NOT NULL AND SET DEFAULT
-- ============================================================================

-- Make the new column NOT NULL
ALTER TABLE mentorship_cycles
ALTER COLUMN cycle_status_id SET NOT NULL;

-- Set default value (1 = 'draft')
ALTER TABLE mentorship_cycles
ALTER COLUMN cycle_status_id SET DEFAULT 1;

-- ============================================================================
-- 5. DROP OLD ENUM COLUMN AND TYPE
-- ============================================================================

-- Drop old index first
DROP INDEX IF EXISTS idx_mentorship_cycles_status;

-- Drop the old status column
ALTER TABLE mentorship_cycles
DROP COLUMN status;

-- Drop the old ENUM type
DROP TYPE cycle_status;

-- ============================================================================
-- 6. RENAME NEW COLUMN
-- ============================================================================

-- Rename cycle_status_id to status for backward compatibility
ALTER TABLE mentorship_cycles
RENAME COLUMN cycle_status_id TO status;

-- ============================================================================
-- 7. RECREATE INDEX
-- ============================================================================

-- Recreate index for new column (2 = 'open')
CREATE INDEX idx_mentorship_cycles_status
ON mentorship_cycles(status)
WHERE status = 2;

-- ============================================================================
-- 8. VERIFICATION QUERIES (Informational - automatically checked above)
-- ============================================================================
-- To manually verify after migration:
--
-- Check all cycles have valid status IDs:
-- SELECT COUNT(*) FROM mentorship_cycles
-- WHERE status NOT IN (SELECT id FROM cycle_statuses);
-- Expected: 0
--
-- Check status distribution:
-- SELECT cs.name, COUNT(*) as count
-- FROM mentorship_cycles mc
-- JOIN cycle_statuses cs ON mc.status = cs.id
-- GROUP BY cs.name;
--
-- Verify foreign key constraint:
-- SELECT conname, contype, conrelid::regclass, confrelid::regclass
-- FROM pg_constraint
-- WHERE conname = 'fk_mentorship_cycles_status';

-- ============================================================================
-- ROLLBACK INSTRUCTIONS (for reference, do not execute)
-- ============================================================================
-- To rollback this migration:
--
-- 1. Recreate ENUM type:
--    CREATE TYPE cycle_status AS ENUM (
--        'draft', 'open', 'closed', 'in_progress', 'completed', 'cancelled'
--    );
--
-- 2. Add back old column:
--    ALTER TABLE mentorship_cycles ADD COLUMN status_enum cycle_status;
--
-- 3. Migrate data back:
--    UPDATE mentorship_cycles
--    SET status_enum = CASE status
--        WHEN 1 THEN 'draft'::cycle_status
--        WHEN 2 THEN 'open'::cycle_status
--        WHEN 3 THEN 'closed'::cycle_status
--        WHEN 4 THEN 'in_progress'::cycle_status
--        WHEN 5 THEN 'completed'::cycle_status
--        WHEN 6 THEN 'cancelled'::cycle_status
--    END;
--
-- 4. Drop new column:
--    ALTER TABLE mentorship_cycles DROP COLUMN status;
--
-- 5. Rename back:
--    ALTER TABLE mentorship_cycles RENAME COLUMN status_enum TO status;
--
-- 6. Make NOT NULL:
--    ALTER TABLE mentorship_cycles ALTER COLUMN status SET NOT NULL;
--    ALTER TABLE mentorship_cycles ALTER COLUMN status SET DEFAULT 'draft';
--
-- 7. Recreate index:
--    CREATE INDEX idx_mentorship_cycles_status
--    ON mentorship_cycles(status) WHERE status = 'open';
--
-- 8. Drop cycle_statuses table:
--    DROP TABLE cycle_statuses CASCADE;
-- ============================================================================

-- V17: Add Year Tracking to Mentorship Types
-- Purpose: Track which years mentees/mentors participated in each mentorship type
-- This eliminates the need for the separate mentee_previous_mentorship_types table
-- Related: PR #416 Follow-Up Tasks (Tasks 5, 6)

-- ============================================================================
-- 1. UPDATE mentee_mentorship_types TABLE
-- ============================================================================

-- Add year column with default value to support existing data
ALTER TABLE mentee_mentorship_types
ADD COLUMN cycle_year INTEGER NOT NULL DEFAULT EXTRACT(YEAR FROM CURRENT_TIMESTAMP);

-- Drop existing primary key constraint
ALTER TABLE mentee_mentorship_types
DROP CONSTRAINT mentee_mentorship_types_pkey;

-- Add new composite primary key including year
ALTER TABLE mentee_mentorship_types
ADD PRIMARY KEY (mentee_id, mentorship_type, cycle_year);

-- Add index for querying by year
CREATE INDEX idx_mentee_mentorship_types_year
ON mentee_mentorship_types(cycle_year);

-- Add composite index for common queries (current year registrations)
CREATE INDEX idx_mentee_mentorship_types_current
ON mentee_mentorship_types(mentee_id, cycle_year, mentorship_type);

-- ============================================================================
-- 2. UPDATE mentor_mentorship_types TABLE
-- ============================================================================

-- Add year column with default value to support existing data
ALTER TABLE mentor_mentorship_types
ADD COLUMN cycle_year INTEGER NOT NULL DEFAULT EXTRACT(YEAR FROM CURRENT_TIMESTAMP);

-- Drop existing primary key constraint
ALTER TABLE mentor_mentorship_types
DROP CONSTRAINT mentor_mentorship_types_pkey;

-- Add new composite primary key including year
ALTER TABLE mentor_mentorship_types
ADD PRIMARY KEY (mentor_id, mentorship_type, cycle_year);

-- Add index for querying active mentors by year
CREATE INDEX idx_mentor_mentorship_types_year
ON mentor_mentorship_types(cycle_year);

-- ============================================================================
-- 3. MIGRATE DATA FROM mentee_previous_mentorship_types
-- ============================================================================

-- Migrate existing previous mentorship data to mentee_mentorship_types with previous year
-- This preserves historical data before dropping the redundant table
INSERT INTO mentee_mentorship_types (mentee_id, mentorship_type, cycle_year)
SELECT mentee_id, mentorship_type, EXTRACT(YEAR FROM CURRENT_TIMESTAMP)::INTEGER - 1
FROM mentee_previous_mentorship_types
ON CONFLICT (mentee_id, mentorship_type, cycle_year) DO NOTHING;

-- ============================================================================
-- 4. DROP REDUNDANT mentee_previous_mentorship_types TABLE
-- ============================================================================

-- Now that data is migrated, drop the redundant table
-- Previous mentorships can be queried from mentee_mentorship_types
-- WHERE cycle_year < CURRENT_YEAR
DROP TABLE IF EXISTS mentee_previous_mentorship_types;

-- ============================================================================
-- ROLLBACK INSTRUCTIONS (for reference, do not execute)
-- ============================================================================
-- To rollback this migration:
--
-- 1. Recreate mentee_previous_mentorship_types table:
--    CREATE TABLE IF NOT EXISTS mentee_previous_mentorship_types (
--        mentee_id       INTEGER NOT NULL REFERENCES mentees (mentee_id) ON DELETE CASCADE,
--        mentorship_type INTEGER NOT NULL REFERENCES mentorship_types (id) ON DELETE CASCADE,
--        PRIMARY KEY (mentee_id, mentorship_type)
--    );
--
-- 2. Migrate data back:
--    INSERT INTO mentee_previous_mentorship_types (mentee_id, mentorship_type)
--    SELECT DISTINCT mentee_id, mentorship_type
--    FROM mentee_mentorship_types
--    WHERE cycle_year < EXTRACT(YEAR FROM CURRENT_TIMESTAMP);
--
-- 3. Drop indexes:
--    DROP INDEX IF EXISTS idx_mentee_mentorship_types_year;
--    DROP INDEX IF EXISTS idx_mentee_mentorship_types_current;
--    DROP INDEX IF EXISTS idx_mentor_mentorship_types_year;
--
-- 4. Update primary keys:
--    ALTER TABLE mentee_mentorship_types DROP CONSTRAINT mentee_mentorship_types_pkey;
--    ALTER TABLE mentee_mentorship_types DROP COLUMN cycle_year;
--    ALTER TABLE mentee_mentorship_types ADD PRIMARY KEY (mentee_id, mentorship_type);
--
--    ALTER TABLE mentor_mentorship_types DROP CONSTRAINT mentor_mentorship_types_pkey;
--    ALTER TABLE mentor_mentorship_types DROP COLUMN cycle_year;
--    ALTER TABLE mentor_mentorship_types ADD PRIMARY KEY (mentor_id, mentorship_type);
-- ============================================================================

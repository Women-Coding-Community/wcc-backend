-- V18: Create Mentorship Cycles Management Table
-- Purpose: Move cycle logic from code to database for flexibility and admin control
-- Replaces hardcoded logic in MentorshipService.getCurrentCycle()
-- Related: PR #416 Follow-Up Tasks

-- ============================================================================
-- 1. CREATE ENUM TYPE FOR CYCLE STATUS
-- ============================================================================

CREATE TYPE cycle_status AS ENUM (
    'draft',        -- Cycle created but not yet open for registration
    'open',         -- Registration is currently open
    'closed',       -- Registration has closed
    'in_progress',  -- Cycle is active, mentorship ongoing
    'completed',    -- Cycle has finished successfully
    'cancelled'     -- Cycle was cancelled
);

-- ============================================================================
-- 2. CREATE mentorship_cycles TABLE
-- ============================================================================

CREATE TABLE IF NOT EXISTS mentorship_cycles (
    cycle_id                SERIAL PRIMARY KEY,
    cycle_year              INTEGER NOT NULL,
    mentorship_type         INTEGER NOT NULL REFERENCES mentorship_types(id) ON DELETE RESTRICT,
    cycle_month             INTEGER CHECK (cycle_month >= 1 AND cycle_month <= 12),
    registration_start_date DATE NOT NULL,
    registration_end_date   DATE NOT NULL,
    cycle_start_date        DATE NOT NULL,
    cycle_end_date          DATE,
    status                  cycle_status NOT NULL DEFAULT 'draft',
    max_mentees_per_mentor  INTEGER DEFAULT 5,
    description             TEXT,
    created_at              TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Ensure unique cycle per type per year per month
    CONSTRAINT unique_cycle_per_type_year_month
        UNIQUE (cycle_year, mentorship_type, cycle_month),

    -- Ensure dates are logical
    CONSTRAINT valid_registration_dates
        CHECK (registration_end_date >= registration_start_date),

    CONSTRAINT valid_cycle_dates
        CHECK (cycle_start_date >= registration_start_date)
);

-- ============================================================================
-- 3. CREATE INDEXES FOR PERFORMANCE
-- ============================================================================

-- Index for finding current open cycles (most common query)
CREATE INDEX idx_mentorship_cycles_status
ON mentorship_cycles(status)
WHERE status = 'open';

-- Index for finding cycles by year and type
CREATE INDEX idx_mentorship_cycles_year_type
ON mentorship_cycles(cycle_year, mentorship_type);

-- Index for finding cycles by month (for ad-hoc cycles)
CREATE INDEX idx_mentorship_cycles_month
ON mentorship_cycles(cycle_month)
WHERE cycle_month IS NOT NULL;

-- ============================================================================
-- 4. SEED 2026 CYCLES
-- ============================================================================

-- Insert Long-Term cycle for 2026 (March)
INSERT INTO mentorship_cycles (
    cycle_year,
    mentorship_type,
    cycle_month,
    registration_start_date,
    registration_end_date,
    cycle_start_date,
    cycle_end_date,
    status,
    max_mentees_per_mentor,
    description
) VALUES (
    2026,
    2,                          -- LONG_TERM type
    3,                          -- March
    '2026-03-01',
    '2026-03-10',
    '2026-03-15',
    '2026-08-31',
    'open',                     -- Currently open for registration
    5,
    'Long-term mentorship program March-August 2026'
);

-- Insert Ad-Hoc cycles for 2026 (May-November)
INSERT INTO mentorship_cycles (
    cycle_year,
    mentorship_type,
    cycle_month,
    registration_start_date,
    registration_end_date,
    cycle_start_date,
    cycle_end_date,
    status,
    max_mentees_per_mentor,
    description
) VALUES
    (2026, 1, 5, '2026-05-01', '2026-05-10', '2026-05-15', '2026-05-31', 'draft', 5, 'Ad-hoc mentorship May 2026'),
    (2026, 1, 6, '2026-06-01', '2026-06-10', '2026-06-15', '2026-06-30', 'draft', 5, 'Ad-hoc mentorship June 2026'),
    (2026, 1, 7, '2026-07-01', '2026-07-10', '2026-07-15', '2026-07-31', 'draft', 5, 'Ad-hoc mentorship July 2026'),
    (2026, 1, 8, '2026-08-01', '2026-08-10', '2026-08-15', '2026-08-31', 'draft', 5, 'Ad-hoc mentorship August 2026'),
    (2026, 1, 9, '2026-09-01', '2026-09-10', '2026-09-15', '2026-09-30', 'draft', 5, 'Ad-hoc mentorship September 2026'),
    (2026, 1, 10, '2026-10-01', '2026-10-10', '2026-10-15', '2026-10-31', 'draft', 5, 'Ad-hoc mentorship October 2026'),
    (2026, 1, 11, '2026-11-01', '2026-11-10', '2026-11-15', '2026-11-30', 'draft', 5, 'Ad-hoc mentorship November 2026');

-- ============================================================================
-- 5. ADD TRIGGER FOR UPDATED_AT TIMESTAMP
-- ============================================================================

CREATE OR REPLACE FUNCTION update_mentorship_cycles_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_mentorship_cycles_timestamp
BEFORE UPDATE ON mentorship_cycles
FOR EACH ROW
EXECUTE FUNCTION update_mentorship_cycles_updated_at();

-- ============================================================================
-- ROLLBACK INSTRUCTIONS (for reference, do not execute)
-- ============================================================================
-- To rollback this migration:
--
-- 1. Drop trigger and function:
--    DROP TRIGGER IF EXISTS trigger_update_mentorship_cycles_timestamp ON mentorship_cycles;
--    DROP FUNCTION IF EXISTS update_mentorship_cycles_updated_at();
--
-- 2. Drop indexes:
--    DROP INDEX IF EXISTS idx_mentorship_cycles_status;
--    DROP INDEX IF EXISTS idx_mentorship_cycles_year_type;
--    DROP INDEX IF EXISTS idx_mentorship_cycles_month;
--
-- 3. Drop table:
--    DROP TABLE IF EXISTS mentorship_cycles CASCADE;
--
-- 4. Drop enum type:
--    DROP TYPE IF EXISTS cycle_status;
-- ============================================================================

-- V20: Create Mentorship Matches Table
-- Purpose: Track confirmed mentor-mentee pairings with cycle association
-- Supports: Match tracking, session tracking, cancellation management
-- Related: PR #416 Follow-Up Tasks, MVP Requirements

-- ============================================================================
-- 1. CREATE ENUM TYPE FOR MATCH STATUS
-- ============================================================================

CREATE TYPE match_status AS ENUM (
    'active',       -- Currently active mentorship
    'completed',    -- Successfully completed
    'cancelled',    -- Cancelled by either party or admin
    'on_hold'       -- Temporarily paused
);

-- ============================================================================
-- 2. CREATE mentorship_matches TABLE
-- ============================================================================

CREATE TABLE IF NOT EXISTS mentorship_matches (
    match_id            SERIAL PRIMARY KEY,
    mentor_id           INTEGER NOT NULL REFERENCES mentors(mentor_id) ON DELETE CASCADE,
    mentee_id           INTEGER NOT NULL REFERENCES mentees(mentee_id) ON DELETE CASCADE,
    cycle_id            INTEGER NOT NULL REFERENCES mentorship_cycles(cycle_id) ON DELETE CASCADE,
    application_id      INTEGER REFERENCES mentee_applications(application_id) ON DELETE SET NULL,
    match_status        match_status NOT NULL DEFAULT 'active',
    start_date          DATE NOT NULL,
    end_date            DATE,
    expected_end_date   DATE,
    session_frequency   VARCHAR(50),        -- e.g., "Weekly", "Bi-weekly", "Monthly"
    total_sessions      INTEGER DEFAULT 0,
    cancellation_reason TEXT,
    cancelled_by        VARCHAR(50),        -- 'mentor', 'mentee', 'admin'
    cancelled_at        TIMESTAMP WITH TIME ZONE,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Prevent duplicate matches for same mentor-mentee pair in same cycle
    CONSTRAINT unique_mentor_mentee_cycle
        UNIQUE (mentor_id, mentee_id, cycle_id),

    -- Ensure dates are logical
    CONSTRAINT valid_match_dates
        CHECK (end_date IS NULL OR end_date >= start_date),

    -- Ensure expected_end_date is after start_date
    CONSTRAINT valid_expected_end_date
        CHECK (expected_end_date IS NULL OR expected_end_date >= start_date),

    -- Ensure cancellation data is consistent
    CONSTRAINT valid_cancellation_data
        CHECK (
            (match_status = 'cancelled' AND cancelled_by IS NOT NULL AND cancelled_at IS NOT NULL) OR
            (match_status != 'cancelled' AND cancelled_by IS NULL AND cancelled_at IS NULL)
        )
);

-- ============================================================================
-- 3. CREATE INDEXES FOR PERFORMANCE
-- ============================================================================

-- Index for mentor to view their mentees
CREATE INDEX idx_mentorship_matches_mentor
ON mentorship_matches(mentor_id, match_status);

-- Index for mentee to view their mentors
CREATE INDEX idx_mentorship_matches_mentee
ON mentorship_matches(mentee_id, match_status);

-- Index for active matches by cycle (most common query)
CREATE INDEX idx_mentorship_matches_cycle_active
ON mentorship_matches(cycle_id, match_status)
WHERE match_status = 'active';

-- Index for finding all matches in a cycle
CREATE INDEX idx_mentorship_matches_cycle
ON mentorship_matches(cycle_id);

-- Index for tracking which application led to match
CREATE INDEX idx_mentorship_matches_application
ON mentorship_matches(application_id)
WHERE application_id IS NOT NULL;

-- Composite index for mentor capacity queries
CREATE INDEX idx_mentorship_matches_mentor_cycle_status
ON mentorship_matches(mentor_id, cycle_id, match_status);

-- ============================================================================
-- 4. ADD TRIGGER FOR UPDATED_AT TIMESTAMP
-- ============================================================================

CREATE OR REPLACE FUNCTION update_mentorship_matches_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_mentorship_matches_timestamp
BEFORE UPDATE ON mentorship_matches
FOR EACH ROW
EXECUTE FUNCTION update_mentorship_matches_updated_at();

-- ============================================================================
-- 5. ADD TRIGGER TO AUTO-UPDATE CANCELLED_AT
-- ============================================================================

CREATE OR REPLACE FUNCTION update_match_cancellation_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    -- Auto-set cancelled_at when status changes to cancelled
    IF NEW.match_status = 'cancelled'
       AND OLD.match_status != 'cancelled'
       AND NEW.cancelled_at IS NULL THEN
        NEW.cancelled_at = CURRENT_TIMESTAMP;
    END IF;

    -- Auto-set end_date when status changes to completed or cancelled
    IF (NEW.match_status IN ('completed', 'cancelled'))
       AND (OLD.match_status NOT IN ('completed', 'cancelled'))
       AND NEW.end_date IS NULL THEN
        NEW.end_date = CURRENT_DATE;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_match_cancellation_timestamp
BEFORE UPDATE ON mentorship_matches
FOR EACH ROW
WHEN (NEW.match_status IS DISTINCT FROM OLD.match_status)
EXECUTE FUNCTION update_match_cancellation_timestamp();

-- ============================================================================
-- 6. ADD TRIGGER TO ENFORCE MENTOR CAPACITY LIMITS
-- ============================================================================

CREATE OR REPLACE FUNCTION check_mentor_capacity()
RETURNS TRIGGER AS $$
DECLARE
    max_allowed INTEGER;
    current_count INTEGER;
    cycle_description TEXT;
BEGIN
    -- Get max_mentees_per_mentor for this cycle
    SELECT max_mentees_per_mentor, description
    INTO max_allowed, cycle_description
    FROM mentorship_cycles
    WHERE cycle_id = NEW.cycle_id;

    -- Count active matches for this mentor in this cycle
    SELECT COUNT(*)
    INTO current_count
    FROM mentorship_matches
    WHERE mentor_id = NEW.mentor_id
      AND cycle_id = NEW.cycle_id
      AND match_status = 'active'
      AND match_id != COALESCE(NEW.match_id, 0);  -- Exclude current record if updating

    -- Check capacity
    IF current_count >= max_allowed THEN
        RAISE EXCEPTION 'Mentor % has reached maximum capacity (%) for cycle % (%)',
            NEW.mentor_id, max_allowed, NEW.cycle_id, cycle_description;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_enforce_mentor_capacity
BEFORE INSERT OR UPDATE ON mentorship_matches
FOR EACH ROW
WHEN (NEW.match_status = 'active')
EXECUTE FUNCTION check_mentor_capacity();

-- ============================================================================
-- 7. ADD COMMENTS FOR DOCUMENTATION
-- ============================================================================

COMMENT ON TABLE mentorship_matches IS
'Tracks confirmed mentor-mentee pairings with cycle association. Created when mentorship team confirms a match from an accepted application.';

COMMENT ON COLUMN mentorship_matches.application_id IS
'Reference to the mentee_application that led to this match. Can be NULL if match was created manually.';

COMMENT ON COLUMN mentorship_matches.session_frequency IS
'Expected frequency of mentorship sessions (e.g., Weekly, Bi-weekly, Monthly). Informational field.';

COMMENT ON COLUMN mentorship_matches.total_sessions IS
'Total number of completed mentorship sessions. Updated manually or via session tracking feature.';

COMMENT ON COLUMN mentorship_matches.cancelled_by IS
'Who initiated the cancellation: mentor, mentee, or admin.';

COMMENT ON COLUMN mentorship_matches.expected_end_date IS
'Expected end date based on cycle. Actual end_date may differ if cancelled early or extended.';

-- ============================================================================
-- ROLLBACK INSTRUCTIONS (for reference, do not execute)
-- ============================================================================
-- To rollback this migration:
--
-- 1. Drop triggers and functions:
--    DROP TRIGGER IF EXISTS trigger_enforce_mentor_capacity ON mentorship_matches;
--    DROP TRIGGER IF EXISTS trigger_update_match_cancellation_timestamp ON mentorship_matches;
--    DROP TRIGGER IF EXISTS trigger_update_mentorship_matches_timestamp ON mentorship_matches;
--    DROP FUNCTION IF EXISTS check_mentor_capacity();
--    DROP FUNCTION IF EXISTS update_match_cancellation_timestamp();
--    DROP FUNCTION IF EXISTS update_mentorship_matches_updated_at();
--
-- 2. Drop indexes:
--    DROP INDEX IF EXISTS idx_mentorship_matches_mentor;
--    DROP INDEX IF EXISTS idx_mentorship_matches_mentee;
--    DROP INDEX IF EXISTS idx_mentorship_matches_cycle_active;
--    DROP INDEX IF EXISTS idx_mentorship_matches_cycle;
--    DROP INDEX IF EXISTS idx_mentorship_matches_application;
--    DROP INDEX IF EXISTS idx_mentorship_matches_mentor_cycle_status;
--
-- 3. Drop table:
--    DROP TABLE IF EXISTS mentorship_matches CASCADE;
--
-- 4. Drop enum type:
--    DROP TYPE IF EXISTS match_status;
-- ============================================================================

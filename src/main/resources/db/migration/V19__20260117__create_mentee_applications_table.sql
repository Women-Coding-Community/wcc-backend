-- V19: Create Mentee Applications Table
-- Purpose: Track mentee applications to mentors with priority ranking and workflow status
-- Supports: Priority-based mentor selection, application workflow, cycle-specific tracking
-- Related: PR #416 Follow-Up Tasks, MVP Requirements

-- ============================================================================
-- 1. CREATE ENUM TYPE FOR APPLICATION STATUS
-- ============================================================================

CREATE TYPE application_status AS ENUM (
    'pending',              -- Mentee submitted application, awaiting mentor response
    'mentor_reviewing',     -- Mentor is actively reviewing the application
    'mentor_accepted',      -- Mentor accepted (awaiting team confirmation)
    'mentor_declined',      -- Mentor declined this application
    'matched',              -- Successfully matched and confirmed
    'dropped',              -- Mentee withdrew application
    'rejected',             -- Rejected by Mentorship Team
    'expired'               -- Application expired (no response within timeframe)
);

-- ============================================================================
-- 2. CREATE mentee_applications TABLE
-- ============================================================================

CREATE TABLE IF NOT EXISTS mentee_applications (
    application_id       SERIAL PRIMARY KEY,
    mentee_id            INTEGER NOT NULL REFERENCES mentees(mentee_id) ON DELETE CASCADE,
    mentor_id            INTEGER NOT NULL REFERENCES mentors(mentor_id) ON DELETE CASCADE,
    cycle_id             INTEGER NOT NULL REFERENCES mentorship_cycles(cycle_id) ON DELETE CASCADE,
    priority_order       INTEGER NOT NULL CHECK (priority_order >= 1 AND priority_order <= 5),
    application_status   application_status NOT NULL DEFAULT 'pending',
    application_message  TEXT,
    applied_at           TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    reviewed_at          TIMESTAMP WITH TIME ZONE,
    matched_at           TIMESTAMP WITH TIME ZONE,
    mentor_response      TEXT,
    created_at           TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Prevent duplicate applications to same mentor in same cycle
    CONSTRAINT unique_mentee_mentor_cycle
        UNIQUE (mentee_id, mentor_id, cycle_id),

    -- Prevent duplicate priority orders for same mentee in cycle
    -- (Each mentee can only have one application at priority 1, one at priority 2, etc.)
    CONSTRAINT unique_mentee_cycle_priority
        UNIQUE (mentee_id, cycle_id, priority_order)
);

-- ============================================================================
-- 3. CREATE INDEXES FOR PERFORMANCE
-- ============================================================================

-- Index for mentee to view their applications
CREATE INDEX idx_mentee_applications_mentee
ON mentee_applications(mentee_id, cycle_id);

-- Index for mentor to view applications to them
CREATE INDEX idx_mentee_applications_mentor
ON mentee_applications(mentor_id, application_status);

-- Index for finding pending applications (most common query)
CREATE INDEX idx_mentee_applications_pending
ON mentee_applications(application_status)
WHERE application_status IN ('pending', 'mentor_reviewing');

-- Index for priority-based queries (auto-notify next priority)
CREATE INDEX idx_mentee_applications_priority
ON mentee_applications(mentee_id, cycle_id, priority_order);

-- Index for cycle-based queries (admin view)
CREATE INDEX idx_mentee_applications_cycle
ON mentee_applications(cycle_id, application_status);

-- Composite index for mentor dashboard queries
CREATE INDEX idx_mentee_applications_mentor_cycle
ON mentee_applications(mentor_id, cycle_id, application_status);

-- ============================================================================
-- 4. ADD TRIGGER FOR UPDATED_AT TIMESTAMP
-- ============================================================================

CREATE OR REPLACE FUNCTION update_mentee_applications_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_mentee_applications_timestamp
BEFORE UPDATE ON mentee_applications
FOR EACH ROW
EXECUTE FUNCTION update_mentee_applications_updated_at();

-- ============================================================================
-- 5. ADD TRIGGER TO AUTO-UPDATE TIMESTAMPS ON STATUS CHANGE
-- ============================================================================

CREATE OR REPLACE FUNCTION update_application_status_timestamps()
RETURNS TRIGGER AS $$
BEGIN
    -- Update reviewed_at when status changes to mentor_accepted or mentor_declined
    IF (NEW.application_status IN ('mentor_accepted', 'mentor_declined'))
       AND (OLD.application_status NOT IN ('mentor_accepted', 'mentor_declined'))
       AND NEW.reviewed_at IS NULL THEN
        NEW.reviewed_at = CURRENT_TIMESTAMP;
    END IF;

    -- Update matched_at when status changes to matched
    IF NEW.application_status = 'matched'
       AND OLD.application_status != 'matched'
       AND NEW.matched_at IS NULL THEN
        NEW.matched_at = CURRENT_TIMESTAMP;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_application_status_timestamps
BEFORE UPDATE ON mentee_applications
FOR EACH ROW
WHEN (NEW.application_status IS DISTINCT FROM OLD.application_status)
EXECUTE FUNCTION update_application_status_timestamps();

-- ============================================================================
-- 6. ADD COMMENTS FOR DOCUMENTATION
-- ============================================================================

COMMENT ON TABLE mentee_applications IS
'Tracks mentee applications to mentors with priority ranking and workflow status. Supports priority-based mentor selection (1-5 ranking where 1 is highest priority).';

COMMENT ON COLUMN mentee_applications.priority_order IS
'Priority ranking (1-5) where 1 is highest priority. Mentee can apply to up to 5 mentors with different priorities.';

COMMENT ON COLUMN mentee_applications.application_status IS
'Workflow status: pending → mentor_reviewing → mentor_accepted → matched, or → mentor_declined/rejected/dropped';

COMMENT ON COLUMN mentee_applications.application_message IS
'Message from mentee to mentor explaining why they want this mentor and their learning goals.';

COMMENT ON COLUMN mentee_applications.mentor_response IS
'Optional response from mentor when accepting or declining the application.';

-- ============================================================================
-- ROLLBACK INSTRUCTIONS (for reference, do not execute)
-- ============================================================================
-- To rollback this migration:
--
-- 1. Drop triggers and functions:
--    DROP TRIGGER IF EXISTS trigger_update_application_status_timestamps ON mentee_applications;
--    DROP TRIGGER IF EXISTS trigger_update_mentee_applications_timestamp ON mentee_applications;
--    DROP FUNCTION IF EXISTS update_application_status_timestamps();
--    DROP FUNCTION IF EXISTS update_mentee_applications_updated_at();
--
-- 2. Drop indexes:
--    DROP INDEX IF EXISTS idx_mentee_applications_mentee;
--    DROP INDEX IF EXISTS idx_mentee_applications_mentor;
--    DROP INDEX IF EXISTS idx_mentee_applications_pending;
--    DROP INDEX IF EXISTS idx_mentee_applications_priority;
--    DROP INDEX IF EXISTS idx_mentee_applications_cycle;
--    DROP INDEX IF EXISTS idx_mentee_applications_mentor_cycle;
--
-- 3. Drop table:
--    DROP TABLE IF EXISTS mentee_applications CASCADE;
--
-- 4. Drop enum type:
--    DROP TYPE IF EXISTS application_status;
-- ============================================================================

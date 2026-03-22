-- V21: Create Application Status History Table
-- Purpose: Audit trail for application status transitions
-- Supports: Compliance, debugging, analytics, transparency
-- Related: PR #416 Follow-Up Tasks, MVP Requirements

-- ============================================================================
-- 1. CREATE application_status_history TABLE
-- ============================================================================

CREATE TABLE IF NOT EXISTS application_status_history (
    history_id      SERIAL PRIMARY KEY,
    application_id  INTEGER NOT NULL REFERENCES mentee_applications(application_id) ON DELETE CASCADE,
    old_status      application_status,
    new_status      application_status NOT NULL,
    changed_by_id   INTEGER REFERENCES user_accounts(id) ON DELETE SET NULL,
    changed_by_role VARCHAR(50),        -- 'mentor', 'mentee', 'mentorship_team', 'system'
    notes           TEXT,               -- Optional notes explaining the status change
    changed_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 2. CREATE INDEXES FOR PERFORMANCE
-- ============================================================================

-- Index for querying history by application (most common query)
CREATE INDEX idx_application_status_history_app
ON application_status_history(application_id, changed_at DESC);

-- Index for finding who made changes (admin audit queries)
CREATE INDEX idx_application_status_history_user
ON application_status_history(changed_by_id)
WHERE changed_by_id IS NOT NULL;

-- Index for finding changes by role (analytics queries)
CREATE INDEX idx_application_status_history_role
ON application_status_history(changed_by_role)
WHERE changed_by_role IS NOT NULL;

-- Index for finding recent status changes (dashboard queries)
CREATE INDEX idx_application_status_history_recent
ON application_status_history(changed_at DESC);

-- ============================================================================
-- 3. CREATE TRIGGER TO AUTO-LOG STATUS CHANGES
-- ============================================================================

CREATE OR REPLACE FUNCTION log_application_status_change()
RETURNS TRIGGER AS $$
BEGIN
    -- Only log if status actually changed
    IF NEW.application_status IS DISTINCT FROM OLD.application_status THEN
        INSERT INTO application_status_history (
            application_id,
            old_status,
            new_status,
            changed_by_role,
            notes
        ) VALUES (
            NEW.application_id,
            OLD.application_status,
            NEW.application_status,
            'system',  -- Default to system, can be updated by service layer
            'Status automatically changed from ' || OLD.application_status || ' to ' || NEW.application_status
        );
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_log_application_status_change
AFTER UPDATE ON mentee_applications
FOR EACH ROW
WHEN (NEW.application_status IS DISTINCT FROM OLD.application_status)
EXECUTE FUNCTION log_application_status_change();

-- ============================================================================
-- 4. CREATE FUNCTION TO MANUALLY LOG STATUS CHANGE
-- ============================================================================

CREATE OR REPLACE FUNCTION log_status_change(
    p_application_id INTEGER,
    p_old_status application_status,
    p_new_status application_status,
    p_changed_by_id INTEGER,
    p_changed_by_role VARCHAR(50),
    p_notes TEXT
) RETURNS VOID AS $$
BEGIN
    INSERT INTO application_status_history (
        application_id,
        old_status,
        new_status,
        changed_by_id,
        changed_by_role,
        notes
    ) VALUES (
        p_application_id,
        p_old_status,
        p_new_status,
        p_changed_by_id,
        p_changed_by_role,
        p_notes
    );
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- 5. CREATE VIEW FOR EASY QUERYING
-- ============================================================================

CREATE OR REPLACE VIEW v_application_status_timeline AS
SELECT
    ash.history_id,
    ash.application_id,
    ma.mentee_id,
    ma.mentor_id,
    ma.cycle_id,
    ash.old_status,
    ash.new_status,
    ash.changed_by_id,
    ua.email AS changed_by_email,
    ash.changed_by_role,
    ash.notes,
    ash.changed_at,
    mc.cycle_year,
    mc.description AS cycle_description
FROM application_status_history ash
JOIN mentee_applications ma ON ash.application_id = ma.application_id
LEFT JOIN user_accounts ua ON ash.changed_by_id = ua.id
JOIN mentorship_cycles mc ON ma.cycle_id = mc.cycle_id
ORDER BY ash.changed_at DESC;

-- ============================================================================
-- 6. ADD COMMENTS FOR DOCUMENTATION
-- ============================================================================

COMMENT ON TABLE application_status_history IS
'Audit trail for all application status transitions. Automatically logged via trigger and can be manually logged via service layer.';

COMMENT ON COLUMN application_status_history.old_status IS
'Previous status before the change. NULL for initial creation.';

COMMENT ON COLUMN application_status_history.changed_by_id IS
'User account ID of who made the change. NULL for system-automated changes.';

COMMENT ON COLUMN application_status_history.changed_by_role IS
'Role of the person/system that made the change: mentor, mentee, mentorship_team, or system.';

COMMENT ON COLUMN application_status_history.notes IS
'Optional notes explaining the reason for the status change. Used for rejection reasons, decline reasons, etc.';

COMMENT ON VIEW v_application_status_timeline IS
'Denormalized view of application status history with related information for easy querying and reporting.';

-- ============================================================================
-- 7. CREATE HELPER FUNCTION TO GET APPLICATION TIMELINE
-- ============================================================================

CREATE OR REPLACE FUNCTION get_application_timeline(p_application_id INTEGER)
RETURNS TABLE (
    status_name application_status,
    changed_at TIMESTAMP WITH TIME ZONE,
    changed_by_email TEXT,
    changed_by_role VARCHAR(50),
    notes TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        ash.new_status AS status_name,
        ash.changed_at,
        ua.email AS changed_by_email,
        ash.changed_by_role,
        ash.notes
    FROM application_status_history ash
    LEFT JOIN user_accounts ua ON ash.changed_by_id = ua.id
    WHERE ash.application_id = p_application_id
    ORDER BY ash.changed_at ASC;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- ROLLBACK INSTRUCTIONS (for reference, do not execute)
-- ============================================================================
-- To rollback this migration:
--
-- 1. Drop function and view:
--    DROP FUNCTION IF EXISTS get_application_timeline(INTEGER);
--    DROP VIEW IF EXISTS v_application_status_timeline;
--    DROP FUNCTION IF EXISTS log_status_change(INTEGER, application_status, application_status, INTEGER, VARCHAR(50), TEXT);
--
-- 2. Drop trigger and function:
--    DROP TRIGGER IF EXISTS trigger_log_application_status_change ON mentee_applications;
--    DROP FUNCTION IF EXISTS log_application_status_change();
--
-- 3. Drop indexes:
--    DROP INDEX IF EXISTS idx_application_status_history_app;
--    DROP INDEX IF EXISTS idx_application_status_history_user;
--    DROP INDEX IF EXISTS idx_application_status_history_role;
--    DROP INDEX IF EXISTS idx_application_status_history_recent;
--
-- 4. Drop table:
--    DROP TABLE IF EXISTS application_status_history CASCADE;
-- ============================================================================

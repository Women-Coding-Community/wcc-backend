-- ============================================================================
-- ADD NEW VALUE TO APPLICATION_STATUS ENUM
-- ============================================================================

ALTER TYPE application_status ADD VALUE 'pending_manual_match';

-- ============================================================================
-- UPDATE TABLE TO ALLOW NULL MENTOR_ID FOR MANUAL MATCH REQUESTS
-- ============================================================================

ALTER TABLE mentee_applications ALTER COLUMN mentor_id DROP NOT NULL;

COMMENT ON COLUMN mentee_applications.mentor_id IS
'ID of the mentor being applied to. NULL for pending_manual_match status.';

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

-- ============================================================================
-- UPDATE TABLE TO ALLOW NULL PRIORITY_ORDER FOR MANUAL MATCH REQUESTS
-- ============================================================================

ALTER TABLE mentee_applications ALTER COLUMN priority_order DROP NOT NULL;

COMMENT ON COLUMN mentee_applications.priority_order IS
'Priority ranking (1-5) where 1 is highest priority. Null for Pending Manual Match Applications. Mentee can apply to up to 5 mentors with different priorities.';

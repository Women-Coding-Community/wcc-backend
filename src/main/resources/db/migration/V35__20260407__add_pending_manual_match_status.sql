-- ============================================================================
-- ADD NEW VALUE TO APPLICATION_STATUS ENUM
-- ============================================================================

ALTER TYPE application_status ADD VALUE 'pending_manual_match';
ALTER TYPE application_status ADD VALUE 'no_match_found';

-- ============================================================================
-- UPDATE TABLE TO ALLOW NULL MENTOR_ID FOR MANUAL MATCH ORNO MATCH FOUND REQUESTS
-- ============================================================================

ALTER TABLE mentee_applications ALTER COLUMN mentor_id DROP NOT NULL;

COMMENT ON COLUMN mentee_applications.mentor_id IS
'ID of the mentor being applied to. NULL for pending_manual_match (or) no_match_found status.';

-- ============================================================================
-- UPDATE TABLE TO ALLOW NULL PRIORITY_ORDER FOR MANUAL MATCH OR NO MATCH FOUNDREQUESTS
-- ============================================================================

ALTER TABLE mentee_applications ALTER COLUMN priority_order DROP NOT NULL;

COMMENT ON COLUMN mentee_applications.priority_order IS
'Priority ranking (1-5) where 1 is highest priority. Null for Pending Manual Match Applications. Mentee can apply to up to 5 mentors with different priorities.';

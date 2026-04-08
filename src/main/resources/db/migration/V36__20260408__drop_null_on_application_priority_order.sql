-- ============================================================================
-- UPDATE TABLE TO ALLOW NULL PRIORITY_ORDER FOR MANUAL MATCH REQUESTS
-- ============================================================================

ALTER TABLE mentee_applications ALTER COLUMN priority_order DROP NOT NULL;

COMMENT ON COLUMN mentee_applications.mentor_id IS
'Priority ranking (1-5) where 1 is highest priority. Null for Pending Manual Match Applications. Mentee can apply to up to 5 mentors with different priorities.';

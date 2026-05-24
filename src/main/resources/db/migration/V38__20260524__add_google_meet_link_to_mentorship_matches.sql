-- V38: Add google_meet_link to mentorship_matches
-- Purpose: Store the unique Google Meet link generated for each confirmed mentor-mentee pair

ALTER TABLE mentorship_matches
    ADD COLUMN google_meet_link VARCHAR(500);

COMMENT ON COLUMN mentorship_matches.google_meet_link IS
'Unique Google Meet link for the mentor-mentee pair, set by admin after match confirmation.';

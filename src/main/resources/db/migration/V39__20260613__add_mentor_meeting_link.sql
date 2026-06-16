-- V38: Add meeting_link to mentor

ALTER TABLE mentors
    ADD COLUMN meeting_link VARCHAR(500);

COMMENT ON COLUMN mentors.meeting_link IS
'Unique Meeting link for the mentor, set by admin for approved mentors.';
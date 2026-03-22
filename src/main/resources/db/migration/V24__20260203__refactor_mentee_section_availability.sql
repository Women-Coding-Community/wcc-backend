-- V24: Refactor MenteeSection Availability Structure
-- Purpose: Separate Long-Term and Ad-Hoc mentorship availability
-- - Long-term: stores numMentee and total hours commitment
-- - Ad-hoc: keeps monthly availability in mentor_availability table

ALTER TABLE mentor_mentee_section
ADD COLUMN long_term_num_mentee INTEGER,
ADD COLUMN long_term_hours INTEGER;

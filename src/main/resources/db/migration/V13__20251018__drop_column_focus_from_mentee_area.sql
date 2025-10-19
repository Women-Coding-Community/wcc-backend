-- Drop focus column from mentor_mentee_section table to avoid duplication
ALTER TABLE mentor_mentee_section
    DROP COLUMN focus;
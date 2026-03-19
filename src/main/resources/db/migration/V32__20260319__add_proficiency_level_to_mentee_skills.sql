-- Add proficiency_level_id to mentee_languages junction table
ALTER TABLE mentee_languages
    ADD COLUMN IF NOT EXISTS proficiency_level_id INTEGER REFERENCES proficiency_levels (id) DEFAULT 1;

-- Ensure proficiency_level_id exists in mentee_technical_areas as well, 
-- although V25 attempted it, it's good to be certain as per current requirements.
ALTER TABLE mentee_technical_areas
    ADD COLUMN IF NOT EXISTS proficiency_level_id INTEGER REFERENCES proficiency_levels (id) DEFAULT 1;

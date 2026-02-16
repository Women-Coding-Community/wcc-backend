-- Update mentors identification and consent preferences
ALTER TABLE members
    ADD COLUMN IF NOT EXISTS indentify_as_women_or_non_binary BOOLEAN DEFAULT TRUE;

-- Update mentors identification and consent preferences
ALTER TABLE mentors
    ADD COLUMN IF NOT EXISTS calendly_link               VARCHAR(2048),
    ADD COLUMN IF NOT EXISTS accept_male_mentee          BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS accept_promote_social_media BOOLEAN DEFAULT FALSE;
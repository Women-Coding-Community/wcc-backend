-- Create proficiency_levels table
CREATE TABLE IF NOT EXISTS proficiency_levels
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- Insert proficiency levels
INSERT INTO proficiency_levels (id, name, description)
VALUES (1, 'Beginner', 'Beginner level'),
       (2, 'Intermediate', 'Intermediate level'),
       (3, 'Advanced', 'Advanced level'),
       (4, 'Expert', 'Expert level');

-- Create pronoun_categories table
CREATE TABLE IF NOT EXISTS pronoun_categories
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- Insert pronoun categories
INSERT INTO pronoun_categories (id, name, description)
VALUES (1, 'Feminine', 'Feminine pronouns'),
       (2, 'Masculine', 'Masculine pronouns'),
       (3, 'Neutral', 'Neutral pronouns'),
       (4, 'Multiple', 'Multiple pronoun sets'),
       (5, 'Neopronouns', 'Neopronouns'),
       (6, 'Any', 'Any pronouns'),
       (7, 'Unspecified', 'Unspecified or prefer not to say');

-- Add pronouns columns to members table
ALTER TABLE members
    ADD COLUMN IF NOT EXISTS pronouns             VARCHAR(100),
    ADD COLUMN IF NOT EXISTS pronoun_category_id  INTEGER REFERENCES pronoun_categories (id);

-- Add proficiency_level_id to mentor_languages junction table
ALTER TABLE mentor_languages
    ADD COLUMN IF NOT EXISTS proficiency_level_id INTEGER REFERENCES proficiency_levels (id) DEFAULT 1;

-- Add proficiency_level_id to mentor_technical_areas junction table
ALTER TABLE mentor_technical_areas
    ADD COLUMN IF NOT EXISTS proficiency_level_id INTEGER REFERENCES proficiency_levels (id) DEFAULT 1;

-- Add proficiency_level_id to mentee_technical_areas junction table (if exists)
ALTER TABLE mentee_technical_areas
    ADD COLUMN IF NOT EXISTS proficiency_level_id INTEGER REFERENCES proficiency_levels (id) DEFAULT 1;

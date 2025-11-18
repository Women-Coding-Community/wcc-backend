-- Table for mentees
CREATE TABLE IF NOT EXISTS mentees
(
    mentee_id        INTEGER PRIMARY KEY REFERENCES members (id) ON DELETE CASCADE,
    profile_status   INTEGER NOT NULL         DEFAULT 4,
    bio              TEXT    NOT NULL,
    years_experience INTEGER                  DEFAULT 0,
    spoken_languages VARCHAR(200),
    created_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_profile_status FOREIGN KEY (profile_status)
        REFERENCES member_statuses (id)
);


-- TABLE Mentee mentorship types (join table)
CREATE TABLE IF NOT EXISTS mentee_mentorship_types
(
    mentee_id       INTEGER NOT NULL REFERENCES mentees (mentee_id) ON DELETE CASCADE,
    mentorship_type INTEGER NOT NULL REFERENCES mentorship_types (id) ON DELETE CASCADE,
    PRIMARY KEY (mentee_id, mentorship_type)
);

-- TABLE Mentee previous mentorship types (join table)
CREATE TABLE IF NOT EXISTS mentee_previous_mentorship_types
(
    mentee_id       INTEGER NOT NULL REFERENCES mentees (mentee_id) ON DELETE CASCADE,
    mentorship_type INTEGER NOT NULL REFERENCES mentorship_types (id) ON DELETE CASCADE,
    PRIMARY KEY (mentee_id, mentorship_type)
);

-- TABLE Mentee languages (join table)
CREATE TABLE mentee_languages
(
    mentee_id   INTEGER REFERENCES mentees (mentee_id) ON DELETE CASCADE,
    language_id INTEGER REFERENCES languages (id) ON DELETE CASCADE,
    PRIMARY KEY (mentee_id, language_id)
);

-- TABLE Mentee technical areas (join table)
CREATE TABLE mentee_technical_areas
(
    mentee_id         INTEGER REFERENCES mentees (mentee_id) ON DELETE CASCADE,
    technical_area_id INTEGER REFERENCES technical_areas (id) ON DELETE CASCADE,
    PRIMARY KEY (mentee_id, technical_area_id)
);

-- 🔹 Seed data inserts

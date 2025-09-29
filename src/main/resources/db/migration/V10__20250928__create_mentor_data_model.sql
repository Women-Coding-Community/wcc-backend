-- TABLE Technical areas
CREATE TABLE IF NOT EXISTS technical_areas
(
    id          SERIAL PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,
    description TEXT
);

-- TABLE Languages
CREATE TABLE IF NOT EXISTS languages
(
    id          SERIAL PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,
    description TEXT
);

-- TABLE Mentorship types
CREATE TABLE IF NOT EXISTS mentorship_types
(
    id          SERIAL PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,
    description TEXT
);

-- Insert new member status PENDING
INSERT INTO member_statuses (id, status)
VALUES (4, 'PENDING');

-- TABLE Mentors (depends on members + member_statuses) default PENDING
CREATE TABLE IF NOT EXISTS mentors
(
    mentor_id        INTEGER PRIMARY KEY REFERENCES members (id) ON DELETE CASCADE,
    profile_status   INTEGER REFERENCES member_statuses (id) NOT NULL DEFAULT 4,
    bio              TEXT                                    NOT NULL,
    years_experience INTEGER                                          DEFAULT 0,
    spoken_languages VARCHAR(200),
    is_available     BOOLEAN                                          DEFAULT TRUE,
    created_at       TIMESTAMP WITH TIME ZONE                         DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP WITH TIME ZONE                         DEFAULT CURRENT_TIMESTAMP
);

-- TABLE Mentor technical areas (join table)
CREATE TABLE mentor_technical_areas
(
    mentor_id         INTEGER REFERENCES mentors (mentor_id) ON DELETE CASCADE,
    technical_area_id INTEGER REFERENCES technical_areas (id) ON DELETE CASCADE,
    PRIMARY KEY (mentor_id, technical_area_id)
);

-- TABLE Mentor languages (join table)
CREATE TABLE mentor_languages
(
    mentor_id   INTEGER REFERENCES mentors (mentor_id) ON DELETE CASCADE,
    language_id INTEGER REFERENCES languages (id) ON DELETE CASCADE,
    PRIMARY KEY (mentor_id, language_id)
);

-- TABLE Mentor mentorship types (join table)
CREATE TABLE IF NOT EXISTS mentor_mentorship_types
(
    mentor_id       INTEGER NOT NULL REFERENCES mentors (mentor_id) ON DELETE CASCADE,
    mentorship_type INTEGER NOT NULL REFERENCES mentorship_types (id) ON DELETE CASCADE,
    PRIMARY KEY (mentor_id, mentorship_type)
);

-- TABLE Mentor availability
CREATE TABLE IF NOT EXISTS mentor_availability
(
    mentor_id INTEGER NOT NULL REFERENCES mentors (mentor_id) ON DELETE CASCADE,
    month     INTEGER NOT NULL,
    hours     INTEGER,
    PRIMARY KEY (mentor_id, month)
);

-- TABLE Mentor mentee section
CREATE TABLE IF NOT EXISTS mentor_mentee_section
(
    mentor_id    INTEGER PRIMARY KEY REFERENCES mentors (mentor_id) ON DELETE CASCADE,
    ideal_mentee TEXT NOT NULL,
    focus        TEXT,
    additional   TEXT,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 🔹 Seed data inserts
-- Insert available technical areas
INSERT INTO technical_areas (id, name, description)
VALUES (1, 'BACKEND', 'Backend development'),
       (2, 'DATA_SCIENCE', 'Data science and analytics'),
       (3, 'DEVOPS', 'DevOps practices and tools'),
       (4, 'FRONTEND', 'Frontend development'),
       (5, 'FULLSTACK', 'Fullstack development'),
       (6, 'MOBILE', 'Mobile development'),
       (7, 'OTHER', 'Other technical areas'),
       (8, 'QA', 'Quality assurance and testing');

-- Insert available programming languages
INSERT INTO languages (id, name, description)
VALUES (1, 'C', 'C language'),
       (2, 'C++', 'C++'),
       (3, 'C#', 'C#'),
       (4, 'Go', 'Go'),
       (5, 'Java', 'Java'),
       (6, 'Javascript', 'Javascript'),
       (7, 'Kotlin', 'Kotlin'),
       (8, 'Php', 'PHP'),
       (9, 'Python', 'Python'),
       (10, 'Ruby', 'Ruby'),
       (11, 'Rust', 'Rust'),
       (12, 'Typescript', 'Typescript'),
       (13, 'Other', 'Other programming languages')
ON CONFLICT (id) DO NOTHING;

-- Insert type of mentorship
INSERT INTO mentorship_types (id, name, description)
VALUES (1, 'AD_HOC', 'Ad-hoc mentorship sessions'),
       (2, 'LONG_TERM', 'Long-term mentorship relationships')
ON CONFLICT (id) DO NOTHING;
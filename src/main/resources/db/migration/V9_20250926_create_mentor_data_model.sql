-- 1) Technical areas
CREATE TABLE IF NOT EXISTS technical_areas
(
    id          SERIAL PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,
    description TEXT
);

-- 2) Languages
CREATE TABLE IF NOT EXISTS languages
(
    id          SERIAL PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,
    description TEXT
);

-- 3) Mentorship types
CREATE TABLE IF NOT EXISTS mentorship_types
(
    id          SERIAL PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,
    description TEXT
);

-- 4) Mentors (depends on members + member_statuses)
CREATE TABLE IF NOT EXISTS mentors
(
    mentor_id        INTEGER PRIMARY KEY REFERENCES members (id) ON DELETE CASCADE,
    profile_status   INTEGER REFERENCES member_statuses (id) NOT NULL,
    bio              TEXT                                    NOT NULL,
    years_experience INTEGER,
    spoken_languages TEXT,
    is_available     BOOLEAN                  DEFAULT TRUE,
    created_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5) Mentor technical areas (join table)
CREATE TABLE mentor_technical_areas
(
    mentor_id         INTEGER REFERENCES mentors (mentor_id) ON DELETE CASCADE,
    technical_area_id INTEGER REFERENCES technical_areas (id) ON DELETE CASCADE,
    PRIMARY KEY (mentor_id, technical_area_id)
);

-- 6) Mentor languages (join table)
CREATE TABLE mentor_languages
(
    mentor_id   INTEGER REFERENCES mentors (mentor_id) ON DELETE CASCADE,
    language_id INTEGER REFERENCES languages (id) ON DELETE CASCADE,
    PRIMARY KEY (mentor_id, language_id)
);

-- 7) Mentor mentorship types (join table)
CREATE TABLE IF NOT EXISTS mentor_mentorship_types
(
    mentor_id       INTEGER NOT NULL REFERENCES mentors (mentor_id) ON DELETE CASCADE,
    mentorship_type INTEGER NOT NULL REFERENCES mentorship_types (id) ON DELETE CASCADE,
    PRIMARY KEY (mentor_id, mentorship_type)
);

-- 8) Mentor availability
CREATE TABLE IF NOT EXISTS mentor_availability
(
    mentor_id INTEGER     NOT NULL REFERENCES mentors (mentor_id) ON DELETE CASCADE,
    month     VARCHAR(20) NOT NULL, -- e.g. "MAY"
    hours     INTEGER,
    PRIMARY KEY (mentor_id, month)
);

-- 9) Mentor mentee section
CREATE TABLE IF NOT EXISTS mentor_mentee_section
(
    mentor_id    INTEGER PRIMARY KEY REFERENCES mentors (mentor_id) ON DELETE CASCADE,
    ideal_mentee TEXT,
    focus        TEXT,
    additional   TEXT,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 10) Hero section
CREATE TABLE hero_sections
(
    id           SERIAL PRIMARY KEY,
    page_name    TEXT  NOT NULL UNIQUE, -- e.g. "mentors", "about_us"
    title        TEXT  NOT NULL,
    subtitle     TEXT,
    images       JSONB,
    custom_style JSONB NOT NULL
);

-- 🔹 Seed data inserts (moved to the end)

INSERT INTO technical_areas (id, name, description)
VALUES (1, 'BACKEND', 'Backend development'),
       (2, 'DATA_SCIENCE', 'Data science and analytics'),
       (3, 'DEVOPS', 'DevOps practices and tools'),
       (4, 'FRONTEND', 'Frontend development'),
       (5, 'FULLSTACK', 'Fullstack development'),
       (6, 'MOBILE', 'Mobile development'),
       (7, 'OTHER', 'Other technical areas'),
       (8, 'QA', 'Quality assurance and testing');

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
       (11, 'Rust', 'Rust')
ON CONFLICT (id) DO NOTHING;

INSERT INTO mentorship_types (id, name, description)
VALUES (1, 'AD_HOC', 'Ad-hoc mentorship sessions'),
       (2, 'LONG_TERM', 'Long-term mentorship relationships'),
       (3, 'STUDY_GROUP', 'Study mentorship')
ON CONFLICT (id) DO NOTHING;

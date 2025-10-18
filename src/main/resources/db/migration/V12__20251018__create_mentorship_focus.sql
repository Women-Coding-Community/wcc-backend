-- TABLE Mentorship  areas
CREATE TABLE IF NOT EXISTS mentorship_focus_areas
(
    id          SERIAL PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,
    description TEXT
);

-- Insert available mentorship focus areas
INSERT INTO mentorship_focus_areas (id, name, description)
VALUES (1, 'SWITCH_CAREER_TO_IT', 'Switch career to IT'),
       (2, 'GROW_BEGINNER_TO_MID', 'Grow from beginner to mid-level'),
       (3, 'GROW_MID_TO_SENIOR', 'Grow from mid-level to senior-level'),
       (4, 'GROW_BEYOND_SENIOR', 'Grow beyond senior level'),
       (5, 'SWITCH_TO_MANAGEMENT', 'Switch from IC to management position'),
       (6, 'CHANGE_SPECIALISATION', 'Change specialisation within IT');

-- TABLE Mentor mentorship focus areas (join table)
CREATE TABLE mentor_mentorship_focus_areas
(
    mentor_id     INTEGER REFERENCES mentors (mentor_id) ON DELETE CASCADE,
    focus_area_id INTEGER REFERENCES mentorship_focus_areas (id) ON DELETE CASCADE,
    PRIMARY KEY (mentor_id, focus_area_id)
);
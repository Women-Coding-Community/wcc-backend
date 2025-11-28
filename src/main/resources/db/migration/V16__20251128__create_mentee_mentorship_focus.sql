
-- TABLE Mentee mentorship focus areas (join table)
CREATE TABLE mentee_mentorship_focus_areas
(
    mentee_id     INTEGER REFERENCES mentees (mentee_id) ON DELETE CASCADE,
    focus_area_id INTEGER REFERENCES mentorship_focus_areas (id) ON DELETE CASCADE,
    PRIMARY KEY (mentee_id, focus_area_id)
);
-- Table for feedback types
CREATE TABLE IF NOT EXISTS feedback_types
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

-- Insert feedback types
INSERT INTO feedback_types (id, name, description)
VALUES (1, 'MENTOR_REVIEW', 'Review of a mentor by a mentee'),
       (2, 'COMMUNITY_GENERAL', 'General feedback about the community'),
       (3, 'MENTORSHIP_PROGRAM', 'Feedback about the mentorship program')
ON CONFLICT (id) DO NOTHING;

-- Feedback table
CREATE TABLE IF NOT EXISTS feedback
(
    id                  BIGSERIAL PRIMARY KEY,
    reviewer_id         INTEGER NOT NULL REFERENCES members (id) ON DELETE CASCADE,
    reviewee_id         INTEGER REFERENCES members (id) ON DELETE SET NULL,
    mentorship_cycle_id INTEGER REFERENCES mentorship_cycles (cycle_id) ON DELETE SET NULL,
    feedback_type_id    INTEGER NOT NULL REFERENCES feedback_types (id),
    rating              INTEGER CHECK (rating >= 0 AND rating <= 5),
    feedback_text       TEXT    NOT NULL,
    feedback_year       INTEGER,
    is_anonymous        BOOLEAN                  DEFAULT TRUE,
    is_approved         BOOLEAN                  DEFAULT FALSE,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT feedback_mentor_review_constraint CHECK (
        (feedback_type_id = 1 AND reviewee_id IS NOT NULL) -- MENTOR_REVIEW
            OR (feedback_type_id IN (2, 3))                -- COMMUNITY_GENERAL or MENTORSHIP_PROGRAM
        )
);

-- Performance indexes
CREATE INDEX IF NOT EXISTS idx_feedback_reviewer ON feedback (reviewer_id);
CREATE INDEX IF NOT EXISTS idx_feedback_reviewee ON feedback (reviewee_id);
CREATE INDEX IF NOT EXISTS idx_feedback_type ON feedback (feedback_type_id);
CREATE INDEX IF NOT EXISTS idx_feedback_anonymous_approved ON feedback (is_anonymous, is_approved);
CREATE INDEX IF NOT EXISTS idx_feedback_year ON feedback (feedback_year);
CREATE INDEX IF NOT EXISTS idx_feedback_cycle ON feedback (mentorship_cycle_id);


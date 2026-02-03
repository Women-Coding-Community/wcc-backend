-- V24: Refactor MenteeSection Availability Structure
-- Purpose: Separate Long-Term and Ad-Hoc mentorship availability
-- - Long-term: stores numMentee and total hours commitment
-- - Ad-hoc: keeps monthly availability in mentor_availability table

-- ============================================================================
-- 1. ADD LONG-TERM COLUMNS TO mentor_mentee_section
-- ============================================================================

ALTER TABLE mentor_mentee_section
ADD COLUMN long_term_num_mentee INTEGER,
ADD COLUMN long_term_hours INTEGER;

-- ============================================================================
-- 2. MIGRATE EXISTING LONG-TERM MENTOR DATA
-- ============================================================================

-- For mentors with LONG_TERM mentorship type (id=2), set default values:
-- - numMentee = 1 (default assumption for existing mentors)
-- - hours = sum of their availability hours (or minimum 2 if no availability)
UPDATE mentor_mentee_section ms
SET long_term_num_mentee = 1,
    long_term_hours = COALESCE(
        (SELECT SUM(hours) FROM mentor_availability ma WHERE ma.mentor_id = ms.mentor_id),
        2
    )
WHERE EXISTS (
    SELECT 1 FROM mentor_mentorship_types mmt 
    WHERE mmt.mentor_id = ms.mentor_id 
    AND mmt.mentorship_type = 2
);

-- ============================================================================
-- 3. CLEAR AD-HOC AVAILABILITY FOR LONG-TERM-ONLY MENTORS
-- ============================================================================

-- Remove availability records for mentors who ONLY have long-term mentorship
-- (keep availability for mentors who also have ad-hoc)
DELETE FROM mentor_availability ma
WHERE NOT EXISTS (
    SELECT 1 FROM mentor_mentorship_types mmt 
    WHERE mmt.mentor_id = ma.mentor_id 
    AND mmt.mentorship_type = 1  -- AD_HOC type id
);

-- ============================================================================
-- 4. ADD CONSTRAINTS
-- ============================================================================

-- Add check constraint: if long_term_num_mentee is set, long_term_hours must also be set
ALTER TABLE mentor_mentee_section
ADD CONSTRAINT chk_long_term_consistency 
CHECK (
    (long_term_num_mentee IS NULL AND long_term_hours IS NULL) OR
    (long_term_num_mentee IS NOT NULL AND long_term_hours IS NOT NULL)
);

-- Add check constraint: long_term_num_mentee must be at least 1
ALTER TABLE mentor_mentee_section
ADD CONSTRAINT chk_long_term_num_mentee_min 
CHECK (long_term_num_mentee IS NULL OR long_term_num_mentee >= 1);

-- Add check constraint: long_term_hours must be at least 2 (minimum commitment)
ALTER TABLE mentor_mentee_section
ADD CONSTRAINT chk_long_term_hours_min 
CHECK (long_term_hours IS NULL OR long_term_hours >= 2);

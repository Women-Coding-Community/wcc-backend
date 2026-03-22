-- Add mentee availability
ALTER TABLE mentees
    ADD COLUMN IF NOT EXISTS available_hs_month INTEGER DEFAULT 1;

-- Add mentee application reason for selection mentor
ALTER TABLE mentee_applications
    ADD COLUMN IF NOT EXISTS why_mentor TEXT;
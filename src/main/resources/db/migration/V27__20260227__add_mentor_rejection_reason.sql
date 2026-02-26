-- V27: Add mentor rejection reason
ALTER TABLE mentors
    ADD COLUMN IF NOT EXISTS rejection_reason VARCHAR(250);

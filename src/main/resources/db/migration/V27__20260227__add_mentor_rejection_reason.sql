-- V27: Add mentor rejection reason
ALTER TABLE mentors
    ADD COLUMN IF NOT EXISTS rejection_reason VARCHAR(250);

-- Insert new member status PENDING
INSERT INTO member_statuses (id, status)
VALUES (4, 'PENDING');
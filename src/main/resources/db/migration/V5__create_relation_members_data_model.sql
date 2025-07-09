-- Remove the member_type_id column from members table
ALTER TABLE members
    DROP COLUMN IF EXISTS member_type_id;

-- Create join table for many-to-many relationship between members and member_types
CREATE TABLE IF NOT EXISTS member_member_types
(
    PRIMARY KEY (member_id, member_type_id),
    member_id      INTEGER NOT NULL REFERENCES members (id) ON DELETE CASCADE,
    member_type_id INTEGER NOT NULL REFERENCES member_types (id)

);
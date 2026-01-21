UPDATE role_types
SET name        = 'CONTRIBUTOR',
    description = 'Collaborator In Community'
WHERE id = 2;

-- Insert Role Types
INSERT INTO role_types (id, name, description)
VALUES (5, 'MENTEE', 'Mentee In Community'),
       (6, 'MENTOR', 'Mentor In Community'),
       (7, 'MEMBER', 'Member In Community');
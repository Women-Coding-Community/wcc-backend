UPDATE role_types
SET name        = 'CONTRIBUTOR',
    description = 'Collaborator In Community'
WHERE id = 2;

-- Insert Role Types
INSERT INTO role_types (id, name, description)
VALUES (4, 'LEADER', 'Leader In Community'),
       (5, 'MENTEE', 'Mentee In Community'),
       (6, 'MENTOR', 'Mentor In Community'),
       (7, 'MEMBER', 'Member In Community');

-- Insert a member
INSERT INTO members (full_name, slack_name, position, company_name, email, city, country_id,
                     status_id, bio, years_experience, spoken_language)
values ('Sonali Goel', 'sonaligoel', 'Senior Software Engineer', 'Tesco Technology',
        'sonali.learn.ai@gmail.com', 'London', 234, 1,
        'Passionate about technology and community building.', 10, 'English, Hindi');

-- Insert user account for the new member
INSERT INTO user_accounts (member_id, email, password_hash, enabled)
VALUES ((SELECT id FROM members WHERE members.email = 'sonali.learn.ai@gmail.com'),
        'sonali.learn.ai@gmail.com',
        '$argon2id$v=19$m=65536,t=3,p=1$49crq1CpGyILHW2LRfdpRg$mIaxgAa7ksupTF49pjkONlD2U3i48m2jmbeXeWvRJno',
        TRUE);

-- Link the new member to the LEADER role
INSERT INTO user_roles (user_id, role_id)
VALUES ((SELECT id FROM user_accounts WHERE email = 'sonali.learn.ai@gmail.com'),
        (SELECT id FROM role_types WHERE name = 'LEADER'));
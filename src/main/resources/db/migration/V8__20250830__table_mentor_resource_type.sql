-- Table for mentorship resource types
CREATE TABLE IF NOT EXISTS mentorship_resource_type
(   id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

-- Insert default mentorship resource types
INSERT INTO mentorship_resource_type (id, name)
VALUES (1, 'BOOK_LIST'), (2, 'LINKS'), (3, 'ASSETS');

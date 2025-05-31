CREATE database wcc;

-- Connect to the database
-- \c wcc

CREATE TABLE IF NOT EXISTS page(id TEXT PRIMARY KEY, data JSONB NOT NULL);
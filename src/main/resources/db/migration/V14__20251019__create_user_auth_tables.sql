-- Description: Create tables for user authentication and authorization

DROP TABLE IF EXISTS public.permission_types;

-- User accounts table
CREATE TABLE IF NOT EXISTS user_accounts
(
    id            SERIAL PRIMARY KEY,
    member_id     INTEGER REFERENCES members (id) ON DELETE CASCADE,
    email         TEXT    NOT NULL UNIQUE,
    password_hash TEXT    NOT NULL,
    enabled       BOOLEAN NOT NULL         DEFAULT TRUE,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE role_types
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(25) UNIQUE NOT NULL,
    description VARCHAR(100)       NOT NULL
);

CREATE TABLE user_roles
(
    user_id INTEGER NOT NULL REFERENCES user_accounts (id) ON DELETE CASCADE,
    role_id INTEGER NOT NULL REFERENCES role_types (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Tokens table (opaque tokens with TTL)
CREATE TABLE IF NOT EXISTS user_tokens
(
    token      VARCHAR(128) PRIMARY KEY,
    user_id    INTEGER                  NOT NULL REFERENCES user_accounts (id) ON DELETE CASCADE,
    issued_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked    BOOLEAN                  NOT NULL DEFAULT FALSE
);

-- Index to quickly purge expired tokens
CREATE INDEX IF NOT EXISTS idx_user_tokens_expires_at ON user_tokens (expires_at);

-- Insert Role Types
INSERT INTO role_types (id, name, description)
VALUES (1, 'ADMIN', 'Platform Administrator'),
       (2, 'MEMBER', 'Community Member'),

       (20, 'MENTORSHIP_ADMIN', 'Mentorship Administrator'),
       (21, 'MENTORSHIP_EDITOR', 'Mentorship Team'),

       (30, 'MAIL_ADMIN', 'Newsletter Administrator'),
       (31, 'MAIL_EDITOR', 'Newsletter Editor'),
       (32, 'MAIL_SUBSCRIBER', 'Newsletter Subscriber Coordinator'),
       (33, 'MAIL_PUBLISHER', 'Newsletter Publisher'),
       (34, 'MAIL_VIEWER', 'Newsletter Viewer'),

       (40, 'CONTENT_ADMIN', 'Website Content Administrator'),
       (41, 'CONTENT_EDITOR', 'Website Content Editor'),
       (42, 'CONTENT_VIEWER', 'Website Content Viewer');
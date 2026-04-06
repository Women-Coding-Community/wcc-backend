-- Description: Create table for password reset tokens

CREATE TABLE IF NOT EXISTS password_reset_tokens
(
    token      VARCHAR(128)             NOT NULL PRIMARY KEY,
    user_id    INTEGER                  NOT NULL REFERENCES user_accounts (id) ON DELETE CASCADE,
    issued_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used       BOOLEAN                  NOT NULL DEFAULT FALSE
);

-- Index to quickly purge expired tokens
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_expires_at ON password_reset_tokens (expires_at);

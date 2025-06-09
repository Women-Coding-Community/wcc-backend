-- Table for countries
CREATE TABLE countries
(
    id           SERIAL PRIMARY KEY,
    country_code VARCHAR(2) UNIQUE NOT NULL,
    country_name VARCHAR(100)      NOT NULL
);

-- Table for image types (Profile, Cover, etc)
CREATE TABLE image_types
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Table for members
CREATE TABLE members
(
    id               SERIAL PRIMARY KEY,
    full_name        VARCHAR(255)                             NOT NULL,
    member_type_id   INTEGER REFERENCES member_types (id),
    slack_name       VARCHAR(255)                             NOT NULL,
    position         VARCHAR(255)                             NOT NULL,
    company_name     VARCHAR(255),
    email            VARCHAR(255) UNIQUE,
    city             VARCHAR(100),
    country_id       INTEGER REFERENCES countries (id)        NOT NULL,
    status_id        INTEGER REFERENCES member_statuses (id)  NOT NULL,
    bio              TEXT,
    years_experience INTEGER,
    spoken_language  TEXT,
    permission_id    INTEGER REFERENCES permission_types (id) NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for member images
CREATE TABLE member_images
(
    id            SERIAL PRIMARY KEY,
    member_id     INTEGER REFERENCES members (id) ON DELETE CASCADE,
    image_type_id INTEGER REFERENCES image_types (id),
    image_url     VARCHAR(500) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for member networks
CREATE TABLE member_networks
(
    id              SERIAL PRIMARY KEY,
    member_id       INTEGER REFERENCES members (id) ON DELETE CASCADE,
    network_type_id INTEGER REFERENCES network_types (id),
    link            VARCHAR(500) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for member statuses (Active, Inactive, Banned, etc)
CREATE TABLE member_statuses
(
    id     SERIAL PRIMARY KEY,
    status VARCHAR(50) UNIQUE NOT NULL
);

-- Table for member types (Collaborator, Volunteer, Director, Lead, Evangelist, etc.)
CREATE TABLE member_types
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- Table for network types (Twitter, LinkedIn, Medium, etc)
CREATE TABLE network_types
(
    id      SERIAL PRIMARY KEY,
    network VARCHAR(100) UNIQUE NOT NULL
);

-- Table for permission types (Read, Write, Admin, etc)
CREATE TABLE permission_types
(
    id         SERIAL PRIMARY KEY,
    permission VARCHAR(100) UNIQUE NOT NULL
);
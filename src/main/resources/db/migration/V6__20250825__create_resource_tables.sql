CREATE TABLE IF NOT EXISTS resource_type (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS resource (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    file_name TEXT NOT NULL,
    content_type TEXT NOT NULL,
    size BIGINT NOT NULL,
    drive_file_id TEXT NOT NULL,
    drive_file_link TEXT NOT NULL,
    resource_type_id INTEGER REFERENCES resource_type(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS mentor_profile_picture (
    id UUID PRIMARY KEY,
    mentor_email TEXT NOT NULL UNIQUE,
    resource_id UUID NOT NULL REFERENCES resource(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Insert default resource types
INSERT INTO resource_type (name, description) VALUES 
('PROFILE_PICTURE', 'Profile pictures for mentors'),
('IMAGE', 'General image resources'),
('PDF', 'PDF documents'),
('PRESENTATION', 'PowerPoint or other presentation files'),
('OTHER', 'Other resource types');
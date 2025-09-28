-- Table for member profile picture

DROP TABLE IF EXISTS public.mentor_profile_picture;

CREATE TABLE IF NOT EXISTS public.member_profile_picture (
    member_id INTEGER NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    resource_id UUID NOT NULL REFERENCES resource(id) ON DELETE CASCADE,
    CONSTRAINT mentor_profile_picture_pk PRIMARY KEY (resource_id, member_id)
);

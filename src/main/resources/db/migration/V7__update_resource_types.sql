-- Insert default resource types

UPDATE resource_type SET name = 'EVENT_IMAGE' WHERE  id = 2;
UPDATE resource_type SET name = 'EVENT_PDF' WHERE  id = 3;
UPDATE resource_type SET name = 'EVENT_PRESENTATION' WHERE  id = 4;

INSERT INTO resource_type (id, name, description) VALUES
(6, 'MENTOR_RESOURCE', 'Mentor resource'),
(7, 'IMAGE', 'General image resource'),
(8, 'RESOURCE', 'General resource');

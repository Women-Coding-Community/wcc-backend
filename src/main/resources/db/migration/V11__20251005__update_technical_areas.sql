-- Update technical areas
UPDATE technical_areas
SET name        = 'MOBILE_ANDROID',
    description = 'Android'
WHERE id = 6;

UPDATE technical_areas
SET name        = 'DEVOPS',
    description = 'DevOps / Site Reliability Engineer'
WHERE id = 3;

-- Insert available technical areas
INSERT INTO technical_areas (id, name, description)
VALUES (9, 'DISTRIBUTED_SYSTEMS', 'Distributed Systems'),
       (10, 'DATA_ENGINEERING', 'Data Engineering'),
       (11, 'MOBILE_IOS', 'iOS'),
       (12, 'BUSINESS_ANALYSIS', 'Business Analysis'),
       (13, 'PROD_MANAGEMENT', 'Product Management'),
       (14, 'PROJ_MANAGEMENT', 'Project Management'),
       (15, 'MACHINE_LEARNING', 'Machine Learning'),
       (16, 'ENGINEERING_MANAGEMENT', 'Engineering management'),
       (17, 'CLOUD_ENGINEER', 'Cloud Engineer');
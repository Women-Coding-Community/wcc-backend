-- Test-only minimal reference data seeding to support integration tests
-- This file is loaded from src/testInt/resources and picked up by Flyway (classpath:db/migration)

-- image_types (ensure 'desktop' exists as repositories look it up by type)
INSERT INTO image_types (id, type)
SELECT 1, 'desktop'
WHERE NOT EXISTS (SELECT 1 FROM image_types WHERE id = 1 OR LOWER(type) = 'desktop');

-- Also add 'mobile' to satisfy potential lookups
INSERT INTO image_types (id, type)
SELECT 2, 'mobile'
WHERE NOT EXISTS (SELECT 1 FROM image_types WHERE id = 2 OR LOWER(type) = 'mobile');

-- member_statuses: ensure id=1 ACTIVE exists (MemberMapper uses defaultStatusId = 1)
INSERT INTO member_statuses (id, status)
SELECT 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM member_statuses WHERE id = 1);

-- member_types: ensure MENTOR with id=6 exists (code uses enum id mapping)
INSERT INTO member_types (id, name)
SELECT 6, 'MENTOR'
WHERE NOT EXISTS (SELECT 1 FROM member_types WHERE id = 6 OR UPPER(name) = 'MENTOR');

-- social_network_types: ensure LINKEDIN with id=6 exists
INSERT INTO social_network_types (id, type)
SELECT 6, 'LINKEDIN'
WHERE NOT EXISTS (SELECT 1 FROM social_network_types WHERE id = 6 OR UPPER(type) = 'LINKEDIN');

-- countries: ensure ES (Spain) and GB (United Kingdom) exist (looked up by code)
INSERT INTO countries (country_code, country_name)
SELECT 'ES', 'Spain'
WHERE NOT EXISTS (SELECT 1 FROM countries WHERE UPPER(country_code) = 'ES');

INSERT INTO countries (country_code, country_name)
SELECT 'GB', 'United Kingdom'
WHERE NOT EXISTS (SELECT 1 FROM countries WHERE UPPER(country_code) = 'GB');

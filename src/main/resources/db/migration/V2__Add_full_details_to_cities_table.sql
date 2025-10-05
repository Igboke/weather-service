ALTER TABLE cities
ADD COLUMN country VARCHAR(255),
ADD COLUMN latitude DOUBLE PRECISION,
ADD COLUMN longitude DOUBLE PRECISION,
ADD COLUMN last_searched TIMESTAMP,
ADD COLUMN search_count INT;

UPDATE cities SET country = '' WHERE country IS NULL;
UPDATE cities SET latitude = 0.0 WHERE latitude IS NULL;
UPDATE cities SET longitude = 0.0 WHERE longitude IS NULL;
UPDATE cities SET search_count = 0 WHERE search_count IS NULL;

ALTER TABLE cities
ALTER COLUMN country SET NOT NULL,
ALTER COLUMN latitude SET NOT NULL,
ALTER COLUMN longitude SET NOT NULL,
ALTER COLUMN search_count SET NOT NULL;

ALTER TABLE cities RENAME COLUMN city_name TO name;
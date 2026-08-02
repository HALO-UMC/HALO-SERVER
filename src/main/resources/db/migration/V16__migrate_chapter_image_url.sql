ALTER TABLE chapter
    ADD long_image_url VARCHAR(255) NULL;

ALTER TABLE chapter
    ADD short_image_url VARCHAR(255) NULL;

UPDATE chapter
SET short_image_url = image_url,
    long_image_url  = image_url;
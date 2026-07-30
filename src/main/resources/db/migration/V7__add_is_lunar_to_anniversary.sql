ALTER TABLE anniversary
    ADD COLUMN is_lunar BOOLEAN NOT NULL DEFAULT FALSE AFTER anniversary_date;
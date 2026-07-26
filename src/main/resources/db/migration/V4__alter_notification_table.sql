ALTER TABLE notification
    ADD COLUMN setting_enabled BOOLEAN NOT NULL;

ALTER TABLE notification
    ADD COLUMN anniversary_enabled BOOLEAN NOT NULL;
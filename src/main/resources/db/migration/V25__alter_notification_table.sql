ALTER TABLE notification
    MODIFY COLUMN anniversary_enabled BOOLEAN NULL;

ALTER TABLE notification
    ADD COLUMN processing_at datetime NULL;

ALTER TABLE notification
    ADD COLUMN processing_lease_id BINARY(16) NULL;
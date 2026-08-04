ALTER TABLE notification
    ADD COLUMN setting_enabled BOOLEAN NULL;

UPDATE notification
SET setting_enabled = TRUE
WHERE setting_enabled IS NULL;

ALTER TABLE notification
    MODIFY COLUMN setting_enabled BOOLEAN NOT NULL;

ALTER TABLE notification
    ADD COLUMN anniversary_enabled BOOLEAN NULL;

UPDATE notification
SET anniversary_enabled = TRUE
WHERE anniversary_enabled IS NULL;

ALTER TABLE notification
    MODIFY COLUMN anniversary_enabled BOOLEAN NOT NULL;

UPDATE notification
SET status = 'EXPIRED'
WHERE status = 'CANCELED';

ALTER TABLE notification
    DROP FOREIGN KEY FK_NOTIFICATION_ON_ANNIVERSARY;

ALTER TABLE notification
    DROP INDEX uk_notification_anniversary_type;

ALTER TABLE notification
    ADD CONSTRAINT uk_notification_anniversary_type_scheduled_at UNIQUE (anniversary_id, notification_type, scheduled_at);

ALTER TABLE notification
    ADD CONSTRAINT FK_NOTIFICATION_ON_ANNIVERSARY FOREIGN KEY (anniversary_id) REFERENCES anniversary (anniversary_id);
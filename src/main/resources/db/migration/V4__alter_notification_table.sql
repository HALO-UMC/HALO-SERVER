ALTER TABLE notification
    ADD COLUMN setting_enabled BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE notification
    ADD COLUMN anniversary_enabled BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE notification
    SET status = 'EXPIRED'
    WHERE status = 'CANCELED';

ALTER TABLE notification
    DROP INDEX uk_notification_anniversary_type;

ALTER TABLE notification
    ADD CONSTRAINT uk_notification_anniversary_type_scheduled_at UNIQUE (anniversary_id, notification_type, scheduled_at);
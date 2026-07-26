ALTER TABLE notification
    DROP COLUMN is_read;

ALTER TABLE notification
    ADD COLUMN scheduled_at DATETIME NULL;

UPDATE notification
SET scheduled_at = created_at
WHERE scheduled_at IS NULL;

ALTER TABLE notification
    MODIFY COLUMN scheduled_at DATETIME NOT NULL;

ALTER TABLE notification
    ADD COLUMN status VARCHAR(20);

UPDATE notification
SET status = 'SCHEDULED';

ALTER TABLE notification
    MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED';

ALTER TABLE notification
    ADD COLUMN anniversary_id BIGINT NULL;

ALTER TABLE notification
    ADD CONSTRAINT FK_NOTIFICATION_ON_ANNIVERSARY FOREIGN KEY (anniversary_id) REFERENCES anniversary (anniversary_id);

ALTER TABLE notification
    ADD CONSTRAINT uk_notification_anniversary_type UNIQUE (anniversary_id, notification_type);
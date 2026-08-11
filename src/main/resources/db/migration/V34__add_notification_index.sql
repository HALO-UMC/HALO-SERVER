ALTER TABLE notification
    ADD INDEX idx_notification_status_scheduled_at (status, scheduled_at);

ALTER TABLE notification
    ADD INDEX idx_notification_status_processing_at (status, processing_at);
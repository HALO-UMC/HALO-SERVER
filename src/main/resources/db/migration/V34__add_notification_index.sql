CREATE INDEX idx_notification_status_scheduled_at
    ON notification (status, scheduled_at);

CREATE INDEX idx_notification_status_processing_at
    ON notification (status, processing_at);
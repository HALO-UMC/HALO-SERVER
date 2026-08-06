ALTER TABLE member_setting
    ADD COLUMN is_all_notification_enabled BIT(1) NOT NULL DEFAULT b'1';
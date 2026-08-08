ALTER TABLE common_anniversary
    ADD COLUMN memo VARCHAR(255) NULL;

UPDATE common_anniversary
SET seven_days_alarm_enabled = false;
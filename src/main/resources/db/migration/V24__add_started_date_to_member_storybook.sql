ALTER TABLE member_storybook
    ADD COLUMN started_date DATE NULL;

UPDATE member_storybook
SET started_date = DATE(created_at)
WHERE started_date IS NULL;

ALTER TABLE member_storybook
    MODIFY COLUMN started_date DATE NOT NULL;
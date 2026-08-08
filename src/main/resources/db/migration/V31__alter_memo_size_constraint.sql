ALTER TABLE common_anniversary
    MODIFY
        COLUMN memo VARCHAR(50) NULL;

ALTER TABLE anniversary
    MODIFY
        COLUMN memo VARCHAR(50) NULL;
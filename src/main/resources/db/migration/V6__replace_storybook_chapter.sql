ALTER TABLE member_chapter
    DROP FOREIGN KEY FK_MEMBER_CHAPTER_ON_STORYBOOK_CHAPTER;

ALTER TABLE storybook_chapter
    DROP FOREIGN KEY FK_STORYBOOK_CHAPTER_ON_CHAPTER;

ALTER TABLE storybook_chapter
    DROP FOREIGN KEY FK_STORYBOOK_CHAPTER_ON_STORYBOOK;

ALTER TABLE chapter
    ADD chapter_order INT NULL;

ALTER TABLE chapter
    ADD storybook_id BIGINT NULL;

UPDATE chapter c
    JOIN storybook_chapter sc ON sc.chapter_id = c.chapter_id
SET c.storybook_id  = sc.storybook_id,
    c.chapter_order = sc.chapter_order;

ALTER TABLE chapter
    MODIFY chapter_order INT NOT NULL;

ALTER TABLE chapter
    MODIFY storybook_id BIGINT NOT NULL;

ALTER TABLE chapter
    ADD CONSTRAINT FK_CHAPTER_ON_STORYBOOK FOREIGN KEY (storybook_id) REFERENCES storybook (storybook_id);

ALTER TABLE member_chapter
    CHANGE storybook_chapter_id chapter_id BIGINT NOT NULL;

ALTER TABLE member_chapter
    ADD CONSTRAINT FK_MEMBER_CHAPTER_ON_CHAPTER FOREIGN KEY (chapter_id) REFERENCES chapter (chapter_id);

DROP TABLE storybook_chapter;
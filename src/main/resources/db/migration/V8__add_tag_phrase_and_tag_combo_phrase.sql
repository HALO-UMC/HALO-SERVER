ALTER TABLE tag ADD COLUMN phrase VARCHAR(100) NULL AFTER description;

CREATE TABLE IF NOT EXISTS tag_combo_phrase (
                                                tag_combo_phrase_id BIGINT NOT NULL AUTO_INCREMENT,
                                                tag_id_1 BIGINT NOT NULL,
                                                tag_id_2 BIGINT NOT NULL,
                                                phrase VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (tag_combo_phrase_id),
    CONSTRAINT uk_tag_combo_phrase_tag1_tag2 UNIQUE (tag_id_1, tag_id_2),
    CONSTRAINT chk_tag_combo_phrase_order CHECK (tag_id_1 < tag_id_2),
    CONSTRAINT fk_tag_combo_phrase_tag1 FOREIGN KEY (tag_id_1) REFERENCES tag(tag_id),
    CONSTRAINT fk_tag_combo_phrase_tag2 FOREIGN KEY (tag_id_2) REFERENCES tag(tag_id)
    );

UPDATE tag t
    JOIN storybook_tag st ON st.tag_id = t.tag_id AND st.priority_level = 'PRIMARY'
    SET t.phrase = st.phrase
WHERE t.phrase IS NULL;

UPDATE tag
SET phrase = title
WHERE category = 'DESIRED_DIRECTION'
  AND phrase IS NULL;

INSERT INTO tag_combo_phrase (tag_id_1, tag_id_2, phrase, created_at, updated_at)
SELECT
    LEAST(primary_st.tag_id, sec_st.tag_id),
    GREATEST(primary_st.tag_id, sec_st.tag_id),
    sec_st.phrase,
    NOW(), NOW()
FROM storybook_tag sec_st
         JOIN storybook_tag primary_st
              ON primary_st.storybook_id = sec_st.storybook_id
                  AND primary_st.priority_level = 'PRIMARY'
WHERE sec_st.priority_level = 'SECONDARY'
    ON DUPLICATE KEY UPDATE phrase = VALUES(phrase);
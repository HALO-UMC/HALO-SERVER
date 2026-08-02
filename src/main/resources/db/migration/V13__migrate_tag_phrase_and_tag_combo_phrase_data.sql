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
UPDATE tag t
    JOIN (
    SELECT tag_id, MIN(phrase) AS phrase
    FROM storybook_tag
    WHERE priority_level = 'PRIMARY'
    GROUP BY tag_id
    ) st ON st.tag_id = t.tag_id
    SET t.phrase = st.phrase
WHERE t.phrase IS NULL;

UPDATE tag
SET phrase = title
WHERE category = 'DESIRED_DIRECTION'
  AND phrase IS NULL;

INSERT INTO tag_combo_phrase (tag_id_1, tag_id_2, phrase, created_at, updated_at)
SELECT
    tag_id_1,
    tag_id_2,
    MIN(phrase),
    NOW(), NOW()
FROM (
         SELECT
             LEAST(primary_st.tag_id, sec_st.tag_id) AS tag_id_1,
             GREATEST(primary_st.tag_id, sec_st.tag_id) AS tag_id_2,
             sec_st.phrase AS phrase
         FROM storybook_tag sec_st
                  JOIN storybook_tag primary_st
                       ON primary_st.storybook_id = sec_st.storybook_id
                           AND primary_st.priority_level = 'PRIMARY'
         WHERE sec_st.priority_level = 'SECONDARY'
     ) combo
GROUP BY tag_id_1, tag_id_2;
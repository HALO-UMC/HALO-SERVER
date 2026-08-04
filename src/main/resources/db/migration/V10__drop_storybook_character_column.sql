-- 대표 행 선정(삭제 위함)
CREATE TEMPORARY TABLE tmp_character AS
SELECT storybook_id, MIN(storybook_character_id) AS keep_id
FROM storybook_character
GROUP BY storybook_id;

DELETE sc
FROM storybook_character sc
    JOIN tmp_character t ON t.storybook_id=sc.storybook_id
WHERE sc.storybook_character_id<>t.keep_id;

DROP TEMPORARY TABLE tmp_character;

ALTER TABLE storybook_character
    DROP COLUMN variant,
    DROP COLUMN image_url;
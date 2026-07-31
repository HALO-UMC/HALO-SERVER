CREATE TABLE IF NOT EXISTS storybook_character_variant
(
    storybook_character_variant_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at                     datetime              NOT NULL,
    updated_at                     datetime              NOT NULL,
    storybook_character_id         BIGINT                NOT NULL,
    variant                        VARCHAR(255)          NOT NULL,
    image_url                      VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_storybook_character_variant PRIMARY KEY (storybook_character_variant_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 대표 행 선정
CREATE TEMPORARY TABLE tmp_character AS
SELECT storybook_id, MIN(storybook_character_id) AS keep_id
FROM storybook_character
GROUP BY storybook_id;

INSERT INTO storybook_character_variant (created_at, updated_at, storybook_character_id, variant, image_url)
SELECT sc.created_at,
       sc.updated_at,
       t.keep_id,
       CASE sc.variant
           WHEN 'ORIGINAL' THEN 'WRITING'
           WHEN 'IMAGE_CHOICE' THEN 'SCENE_CARD'
           ELSE sc.variant
           END,
       sc.image_url
FROM storybook_character sc
         JOIN tmp_character t ON t.storybook_id = sc.storybook_id;

DROP TEMPORARY TABLE tmp_character;
ALTER TABLE storybook
    DROP COLUMN theme_order;

ALTER TABLE tag
    DROP COLUMN phrase;

DROP TABLE IF EXISTS tag_combo_phrase;

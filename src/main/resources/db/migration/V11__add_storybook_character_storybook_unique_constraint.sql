ALTER TABLE storybook_character
    ADD UNIQUE INDEX uk_storybook_character_storybook (storybook_id);
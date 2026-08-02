ALTER TABLE storybook_character_variant
    ADD CONSTRAINT FK_STORYBOOK_CHARACTER_VARIANT_ON_STORYBOOK_CHARACTER
    FOREIGN KEY (storybook_character_id) REFERENCES storybook_character (storybook_character_id);

ALTER TABLE storybook_character_variant
    ADD UNIQUE INDEX uk_storybook_character_variant_character_variant (storybook_character_id, variant);
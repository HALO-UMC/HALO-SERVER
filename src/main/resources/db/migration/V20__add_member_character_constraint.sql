ALTER TABLE member
    ADD CONSTRAINT FK_MEMBER_ON_STORYBOOK_CHARACTER
        FOREIGN KEY (storybook_character_id) REFERENCES storybook_character (storybook_character_id);
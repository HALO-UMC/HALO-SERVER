ALTER TABLE tag_combo_phrase
    ADD CONSTRAINT chk_tag_combo_phrase_order CHECK (tag_id_1 < tag_id_2);
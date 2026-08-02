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
    CONSTRAINT fk_tag_combo_phrase_tag1 FOREIGN KEY (tag_id_1) REFERENCES tag(tag_id),
    CONSTRAINT fk_tag_combo_phrase_tag2 FOREIGN KEY (tag_id_2) REFERENCES tag(tag_id)
    );
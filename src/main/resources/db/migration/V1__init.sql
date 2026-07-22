CREATE TABLE anniversary
(
    anniversary_id           BIGINT AUTO_INCREMENT NOT NULL,
    created_at               datetime              NOT NULL,
    updated_at               datetime              NOT NULL,
    member_id                BIGINT                NOT NULL,
    title                    VARCHAR(20)           NOT NULL,
    anniversary_date         date                  NOT NULL,
    is_repeated              BIT(1)                NOT NULL,
    seven_days_alarm_enabled BIT(1)                NOT NULL,
    day_alarm_enabled        BIT(1)                NOT NULL,
    memo                     VARCHAR(255)          NULL,
    CONSTRAINT pk_anniversary PRIMARY KEY (anniversary_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE bgm
(
    bgm_id     BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NOT NULL,
    updated_at datetime              NOT NULL,
    title      VARCHAR(50)           NOT NULL,
    audio_url  VARCHAR(255)          NOT NULL,
    image_url  VARCHAR(255)          NULL,
    CONSTRAINT pk_bgm PRIMARY KEY (bgm_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE chapter
(
    chapter_id        BIGINT AUTO_INCREMENT NOT NULL,
    created_at        datetime              NOT NULL,
    updated_at        datetime              NOT NULL,
    title             VARCHAR(50)           NOT NULL,
    image_url         VARCHAR(255)          NOT NULL,
    guide             VARCHAR(255)          NULL,
    short_description VARCHAR(255)          NOT NULL,
    `description`     TEXT                  NULL,
    image_description VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_chapter PRIMARY KEY (chapter_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE chapter_question
(
    chapter_question_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at          datetime              NOT NULL,
    updated_at          datetime              NOT NULL,
    chapter_id          BIGINT                NOT NULL,
    question_order      INT                   NOT NULL,
    question            VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_chapter_question PRIMARY KEY (chapter_question_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE common_anniversary
(
    common_anniversary_id    BIGINT AUTO_INCREMENT NOT NULL,
    created_at               datetime              NOT NULL,
    updated_at               datetime              NOT NULL,
    title                    VARCHAR(20)           NOT NULL,
    month                    INT                   NOT NULL,
    day                      INT                   NOT NULL,
    is_lunar                 BIT(1)                NOT NULL,
    seven_days_alarm_enabled BIT(1)                NOT NULL,
    day_alarm_enabled        BIT(1)                NOT NULL,
    CONSTRAINT pk_common_anniversary PRIMARY KEY (common_anniversary_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE member
(
    member_id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at           datetime              NOT NULL,
    updated_at           datetime              NOT NULL,
    name                 VARCHAR(10)           NULL,
    provider             VARCHAR(255)          NULL,
    provider_id          VARCHAR(255)          NULL,
    gender               VARCHAR(255)          NULL,
    birth_date           date                  NULL,
    email                VARCHAR(255)          NULL,
    refresh_token_hash   VARCHAR(64)           NULL,
    onboarding_completed BIT(1)                NOT NULL,
    onboarding_step      INT                   NULL,
    CONSTRAINT pk_member PRIMARY KEY (member_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE member_chapter
(
    member_chapter_id    BIGINT AUTO_INCREMENT NOT NULL,
    created_at           datetime              NOT NULL,
    updated_at           datetime              NOT NULL,
    member_id            BIGINT                NOT NULL,
    storybook_chapter_id BIGINT                NOT NULL,
    scene_card_id        BIGINT                NULL,
    emotion              VARCHAR(255)          NULL,
    cover_type           VARCHAR(255)          NULL,
    image_key            VARCHAR(255)          NULL,
    completed_date       date                  NULL,
    summary              TEXT                  NULL,
    status               VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_member_chapter PRIMARY KEY (member_chapter_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE member_chapter_answer
(
    member_chapter_answer_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at               datetime              NOT NULL,
    updated_at               datetime              NOT NULL,
    member_chapter_id        BIGINT                NOT NULL,
    chapter_question_id      BIGINT                NOT NULL,
    answer                   TEXT                  NOT NULL,
    CONSTRAINT pk_member_chapter_answer PRIMARY KEY (member_chapter_answer_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE member_setting
(
    member_setting_id                  BIGINT AUTO_INCREMENT NOT NULL,
    created_at                         datetime              NOT NULL,
    updated_at                         datetime              NOT NULL,
    member_id                          BIGINT                NOT NULL,
    bgm_id                             BIGINT                NULL,
    bgm_enabled                        BIT(1)                NOT NULL,
    bgm_volume                         INT                   NOT NULL,
    regular_notification_enabled       BIT(1)                NOT NULL,
    regular_notification_time          time                  NOT NULL,
    today_chapter_notification_enabled BIT(1)                NOT NULL,
    retention_notification_enabled     BIT(1)                NOT NULL,
    anniversary_notification_enabled   BIT(1)                NOT NULL,
    CONSTRAINT pk_member_setting PRIMARY KEY (member_setting_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE member_storybook
(
    member_storybook_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at          datetime              NOT NULL,
    updated_at          datetime              NOT NULL,
    member_id           BIGINT                NOT NULL,
    storybook_id        BIGINT                NOT NULL,
    last_chapter_order  INT                   NOT NULL,
    last_completed_date date                  NULL,
    emotion             VARCHAR(255)          NULL,
    CONSTRAINT pk_member_storybook PRIMARY KEY (member_storybook_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE member_tag
(
    member_tag_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at    datetime              NOT NULL,
    updated_at    datetime              NOT NULL,
    tag_id        BIGINT                NOT NULL,
    member_id     BIGINT                NOT NULL,
    CONSTRAINT pk_member_tag PRIMARY KEY (member_tag_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE member_term
(
    member_term_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at     datetime              NOT NULL,
    updated_at     datetime              NOT NULL,
    term_id        BIGINT                NOT NULL,
    member_id      BIGINT                NOT NULL,
    is_agreed      BIT(1)                NOT NULL,
    CONSTRAINT pk_member_term PRIMARY KEY (member_term_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE notification
(
    notification_id   BIGINT AUTO_INCREMENT NOT NULL,
    created_at        datetime              NOT NULL,
    updated_at        datetime              NOT NULL,
    member_id         BIGINT                NOT NULL,
    notification_type VARCHAR(255)          NOT NULL,
    title             VARCHAR(100)          NOT NULL,
    message           VARCHAR(255)          NOT NULL,
    sent_at           datetime              NULL,
    is_read           BIT(1)                NOT NULL,
    CONSTRAINT pk_notification PRIMARY KEY (notification_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE scene_card
(
    scene_card_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at    datetime              NOT NULL,
    updated_at    datetime              NOT NULL,
    chapter_id    BIGINT                NOT NULL,
    image_url     VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_scene_card PRIMARY KEY (scene_card_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE storybook
(
    storybook_id      BIGINT AUTO_INCREMENT NOT NULL,
    created_at        datetime              NOT NULL,
    updated_at        datetime              NOT NULL,
    title             VARCHAR(50)           NOT NULL,
    theme_order       INT                   NOT NULL,
    short_description VARCHAR(255)          NOT NULL,
    `description`     TEXT                  NOT NULL,
    image_url         VARCHAR(255)          NOT NULL,
    spine_color       VARCHAR(7)            NOT NULL,
    CONSTRAINT pk_storybook PRIMARY KEY (storybook_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE storybook_chapter
(
    storybook_chapter_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at           datetime              NOT NULL,
    updated_at           datetime              NOT NULL,
    storybook_id         BIGINT                NOT NULL,
    chapter_id           BIGINT                NOT NULL,
    chapter_order        INT                   NOT NULL,
    CONSTRAINT pk_storybook_chapter PRIMARY KEY (storybook_chapter_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE storybook_character
(
    storybook_character_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at             datetime              NOT NULL,
    updated_at             datetime              NOT NULL,
    storybook_id           BIGINT                NOT NULL,
    name                   VARCHAR(10)           NOT NULL,
    variant                VARCHAR(255)          NOT NULL,
    image_url              VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_storybook_character PRIMARY KEY (storybook_character_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE storybook_tag
(
    storybook_tag_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at       datetime              NOT NULL,
    updated_at       datetime              NOT NULL,
    tag_id           BIGINT                NOT NULL,
    storybook_id     BIGINT                NOT NULL,
    priority_level   VARCHAR(255)          NOT NULL,
    phrase           VARCHAR(50)           NOT NULL,
    CONSTRAINT pk_storybook_tag PRIMARY KEY (storybook_tag_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE tag
(
    tag_id        BIGINT AUTO_INCREMENT NOT NULL,
    created_at    datetime              NOT NULL,
    updated_at    datetime              NOT NULL,
    category      VARCHAR(255)          NOT NULL,
    title         VARCHAR(20)           NOT NULL,
    subtitle      VARCHAR(255)          NULL,
    `description` VARCHAR(255)          NULL,
    CONSTRAINT pk_tag PRIMARY KEY (tag_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE term
(
    term_id           BIGINT AUTO_INCREMENT NOT NULL,
    created_at        datetime              NOT NULL,
    updated_at        datetime              NOT NULL,
    title             VARCHAR(50)           NOT NULL,
    short_description VARCHAR(100)          NOT NULL,
    `description`     TEXT                  NOT NULL,
    is_required       BIT(1)                NOT NULL,
    CONSTRAINT pk_term PRIMARY KEY (term_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

ALTER TABLE member_storybook
    ADD CONSTRAINT uc_3d4b46baa4dcc738ed2e78c26 UNIQUE (member_id, storybook_id);

ALTER TABLE member_tag
    ADD CONSTRAINT uc_6453aa0fba1f2d179a5c0bb1c UNIQUE (tag_id, member_id);

ALTER TABLE member_chapter
    ADD CONSTRAINT uc_f1d860773275ce7753f2c41b6 UNIQUE (member_id, storybook_chapter_id);

ALTER TABLE member
    ADD CONSTRAINT uk_member_provider UNIQUE (provider, provider_id);

ALTER TABLE member_term
    ADD CONSTRAINT uk_member_term UNIQUE (member_id, term_id);

ALTER TABLE storybook_tag
    ADD CONSTRAINT uk_storybook_tag_storybook_tag UNIQUE (storybook_id, tag_id);

ALTER TABLE anniversary
    ADD CONSTRAINT FK_ANNIVERSARY_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (member_id);

ALTER TABLE chapter_question
    ADD CONSTRAINT FK_CHAPTER_QUESTION_ON_CHAPTER FOREIGN KEY (chapter_id) REFERENCES chapter (chapter_id);

ALTER TABLE member_chapter_answer
    ADD CONSTRAINT FK_MEMBER_CHAPTER_ANSWER_ON_CHAPTER_QUESTION FOREIGN KEY (chapter_question_id) REFERENCES chapter_question (chapter_question_id);

ALTER TABLE member_chapter_answer
    ADD CONSTRAINT FK_MEMBER_CHAPTER_ANSWER_ON_MEMBER_CHAPTER FOREIGN KEY (member_chapter_id) REFERENCES member_chapter (member_chapter_id);

ALTER TABLE member_chapter
    ADD CONSTRAINT FK_MEMBER_CHAPTER_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (member_id);

ALTER TABLE member_chapter
    ADD CONSTRAINT FK_MEMBER_CHAPTER_ON_SCENE_CARD FOREIGN KEY (scene_card_id) REFERENCES scene_card (scene_card_id);

ALTER TABLE member_chapter
    ADD CONSTRAINT FK_MEMBER_CHAPTER_ON_STORYBOOK_CHAPTER FOREIGN KEY (storybook_chapter_id) REFERENCES storybook_chapter (storybook_chapter_id);

ALTER TABLE member_setting
    ADD CONSTRAINT FK_MEMBER_SETTING_ON_BGM FOREIGN KEY (bgm_id) REFERENCES bgm (bgm_id);

ALTER TABLE member_setting
    ADD CONSTRAINT FK_MEMBER_SETTING_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (member_id);

ALTER TABLE member_storybook
    ADD CONSTRAINT FK_MEMBER_STORYBOOK_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (member_id);

ALTER TABLE member_storybook
    ADD CONSTRAINT FK_MEMBER_STORYBOOK_ON_STORYBOOK FOREIGN KEY (storybook_id) REFERENCES storybook (storybook_id);

ALTER TABLE member_tag
    ADD CONSTRAINT FK_MEMBER_TAG_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (member_id);

ALTER TABLE member_tag
    ADD CONSTRAINT FK_MEMBER_TAG_ON_TAG FOREIGN KEY (tag_id) REFERENCES tag (tag_id);

ALTER TABLE member_term
    ADD CONSTRAINT FK_MEMBER_TERM_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (member_id);

ALTER TABLE member_term
    ADD CONSTRAINT FK_MEMBER_TERM_ON_TERM FOREIGN KEY (term_id) REFERENCES term (term_id);

ALTER TABLE notification
    ADD CONSTRAINT FK_NOTIFICATION_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (member_id);

ALTER TABLE scene_card
    ADD CONSTRAINT FK_SCENE_CARD_ON_CHAPTER FOREIGN KEY (chapter_id) REFERENCES chapter (chapter_id);

ALTER TABLE storybook_chapter
    ADD CONSTRAINT FK_STORYBOOK_CHAPTER_ON_CHAPTER FOREIGN KEY (chapter_id) REFERENCES chapter (chapter_id);

ALTER TABLE storybook_chapter
    ADD CONSTRAINT FK_STORYBOOK_CHAPTER_ON_STORYBOOK FOREIGN KEY (storybook_id) REFERENCES storybook (storybook_id);

ALTER TABLE storybook_character
    ADD CONSTRAINT FK_STORYBOOK_CHARACTER_ON_STORYBOOK FOREIGN KEY (storybook_id) REFERENCES storybook (storybook_id);

ALTER TABLE storybook_tag
    ADD CONSTRAINT FK_STORYBOOK_TAG_ON_STORYBOOK FOREIGN KEY (storybook_id) REFERENCES storybook (storybook_id);

ALTER TABLE storybook_tag
    ADD CONSTRAINT FK_STORYBOOK_TAG_ON_TAG FOREIGN KEY (tag_id) REFERENCES tag (tag_id);
CREATE TABLE apple_refresh_token
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    datetime              NOT NULL,
    updated_at    datetime              NOT NULL,
    user_id       BIGINT                NOT NULL,
    refresh_token VARCHAR(255)          NULL,
    CONSTRAINT pk_apple_refresh_token PRIMARY KEY (id)
);

CREATE TABLE couple
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    created_at          datetime              NOT NULL,
    updated_at          datetime              NOT NULL,
    user_a_id           BIGINT                NOT NULL,
    user_a_deleted_at   datetime              NULL,
    user_b_id           BIGINT                NOT NULL,
    user_b_deleted_at   datetime              NULL,
    status              VARCHAR(10)           NOT NULL,
    last_question_date  date                  NOT NULL,
    last_question_index BIGINT                NOT NULL,
    CONSTRAINT pk_couple PRIMARY KEY (id)
);

CREATE TABLE couple_keyword
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NOT NULL,
    updated_at datetime              NOT NULL,
    couple_id  BIGINT                NOT NULL,
    keyword_id BIGINT                NOT NULL,
    status     VARCHAR(10)           NOT NULL,
    CONSTRAINT pk_couple_keyword PRIMARY KEY (id)
);

CREATE TABLE couple_question
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime              NOT NULL,
    updated_at  datetime              NOT NULL,
    couple_id   BIGINT                NULL,
    question_id BIGINT                NULL,
    date        date                  NOT NULL,
    CONSTRAINT pk_couple_question PRIMARY KEY (id)
);

CREATE TABLE custom_memory
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NOT NULL,
    updated_at datetime              NOT NULL,
    user_id    BIGINT                NOT NULL,
    couple_id  BIGINT                NOT NULL,
    title      VARCHAR(20)           NOT NULL,
    image_key  VARCHAR(255)          NOT NULL,
    month_key  INT                   NOT NULL,
    CONSTRAINT pk_custom_memory PRIMARY KEY (id)
);

CREATE TABLE fcm_token
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NOT NULL,
    updated_at datetime              NOT NULL,
    user_id    BIGINT                NOT NULL,
    token      VARCHAR(512)          NOT NULL,
    CONSTRAINT pk_fcm_token PRIMARY KEY (id)
);

CREATE TABLE four_cut
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NOT NULL,
    updated_at datetime              NOT NULL,
    user_id    BIGINT                NOT NULL,
    couple_id  BIGINT                NOT NULL,
    image_key  VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_four_cut PRIMARY KEY (id)
);

CREATE TABLE invite_code
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NOT NULL,
    updated_at datetime              NOT NULL,
    user_id    BIGINT                NOT NULL,
    code       VARCHAR(20)           NOT NULL,
    CONSTRAINT pk_invite_code PRIMARY KEY (id)
);

CREATE TABLE keyword
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    datetime              NOT NULL,
    updated_at    datetime              NOT NULL,
    content       VARCHAR(10)           NOT NULL,
    display_order BIGINT                NULL, -- 유지가 필요할까?
    is_default    BIT(1)                NOT NULL,
    CONSTRAINT pk_keyword PRIMARY KEY (id)
);

CREATE TABLE keyword_record
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    created_at        datetime              NOT NULL,
    updated_at        datetime              NOT NULL,
    couple_keyword_id BIGINT                NOT NULL,
    user_id           BIGINT                NOT NULL,
    date              date                  NOT NULL,
    image_key         VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_keyword_record PRIMARY KEY (id)
);

CREATE TABLE question
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    created_at      datetime              NOT NULL,
    updated_at      datetime              NOT NULL,
    content         VARCHAR(255)          NOT NULL,
    question_number BIGINT                NOT NULL,
    CONSTRAINT pk_question PRIMARY KEY (id)
);

CREATE TABLE question_picture
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    created_at         datetime              NOT NULL,
    updated_at         datetime              NOT NULL,
    user_id            BIGINT                NOT NULL,
    couple_question_id BIGINT                NOT NULL,
    image_key          VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_question_picture PRIMARY KEY (id)
);

CREATE TABLE user
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    created_at        datetime              NOT NULL,
    updated_at        datetime              NOT NULL,
    nickname          VARCHAR(50)           NOT NULL,
    provider          VARCHAR(10)           NOT NULL,
    provider_id       VARCHAR(50)           NOT NULL,
    birthday          date                  NULL,
    is_initialized    BIT(1)                NOT NULL,
    profile_image_key VARCHAR(255)          NULL,
    user_status       VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE week_memory
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    created_at      datetime              NOT NULL,
    updated_at      datetime              NOT NULL,
    user_id         BIGINT                NOT NULL,
    couple_id       BIGINT                NOT NULL,
    image_key       VARCHAR(255)          NOT NULL,
    week_start_date date                  NOT NULL,
    week_end_date   date                  NOT NULL,
    month_key       INT                   NOT NULL,
    CONSTRAINT pk_week_memory PRIMARY KEY (id)
);

ALTER TABLE apple_refresh_token
    ADD CONSTRAINT uc_apple_refresh_token_user UNIQUE (user_id);

ALTER TABLE fcm_token
    ADD CONSTRAINT uc_fcm_token_token UNIQUE (token);

ALTER TABLE invite_code
    ADD CONSTRAINT uc_invite_code_code UNIQUE (code);

ALTER TABLE keyword
    ADD CONSTRAINT uc_keyword_content UNIQUE (content);

ALTER TABLE keyword
    ADD CONSTRAINT uc_keyword_display_order UNIQUE (display_order);

ALTER TABLE question
    ADD CONSTRAINT uc_question_question_number UNIQUE (question_number);

ALTER TABLE couple_keyword
    ADD CONSTRAINT uk_couple_keyword UNIQUE (couple_id, keyword_id);

ALTER TABLE keyword_record
    ADD CONSTRAINT uk_keyword_record_user_date UNIQUE (user_id, couple_keyword_id, date);

ALTER TABLE couple
    ADD CONSTRAINT uk_user_a_deleted UNIQUE (user_a_id, user_a_deleted_at);

ALTER TABLE couple
    ADD CONSTRAINT uk_user_b_deleted UNIQUE (user_b_id, user_b_deleted_at);

ALTER TABLE question_picture
    ADD CONSTRAINT uk_user_couple_question UNIQUE (user_id, couple_question_id);

CREATE INDEX idx_couple_created_desc ON couple_question (couple_id, created_at DESC);

CREATE INDEX idx_last_question_date ON couple (last_question_date);

ALTER TABLE apple_refresh_token
    ADD CONSTRAINT FK_APPLE_REFRESH_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE couple_keyword
    ADD CONSTRAINT FK_COUPLE_KEYWORD_ON_COUPLE FOREIGN KEY (couple_id) REFERENCES couple (id);

ALTER TABLE couple_keyword
    ADD CONSTRAINT FK_COUPLE_KEYWORD_ON_KEYWORD FOREIGN KEY (keyword_id) REFERENCES keyword (id);

ALTER TABLE couple
    ADD CONSTRAINT FK_COUPLE_ON_USER_A FOREIGN KEY (user_a_id) REFERENCES user (id);

ALTER TABLE couple
    ADD CONSTRAINT FK_COUPLE_ON_USER_B FOREIGN KEY (user_b_id) REFERENCES user (id);

ALTER TABLE couple_question
    ADD CONSTRAINT FK_COUPLE_QUESTION_ON_COUPLE FOREIGN KEY (couple_id) REFERENCES couple (id);

ALTER TABLE couple_question
    ADD CONSTRAINT FK_COUPLE_QUESTION_ON_QUESTION FOREIGN KEY (question_id) REFERENCES question (id);

ALTER TABLE custom_memory
    ADD CONSTRAINT FK_CUSTOM_MEMORY_ON_COUPLE FOREIGN KEY (couple_id) REFERENCES couple (id);

ALTER TABLE custom_memory
    ADD CONSTRAINT FK_CUSTOM_MEMORY_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE fcm_token
    ADD CONSTRAINT FK_FCM_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE;

ALTER TABLE four_cut
    ADD CONSTRAINT FK_FOUR_CUT_ON_COUPLE FOREIGN KEY (couple_id) REFERENCES couple (id);

ALTER TABLE four_cut
    ADD CONSTRAINT FK_FOUR_CUT_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE invite_code
    ADD CONSTRAINT FK_INVITE_CODE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE keyword_record
    ADD CONSTRAINT FK_KEYWORD_RECORD_ON_COUPLE_KEYWORD FOREIGN KEY (couple_keyword_id) REFERENCES couple_keyword (id);

ALTER TABLE keyword_record
    ADD CONSTRAINT FK_KEYWORD_RECORD_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE question_picture
    ADD CONSTRAINT FK_QUESTION_PICTURE_ON_COUPLE_QUESTION FOREIGN KEY (couple_question_id) REFERENCES couple_question (id);

ALTER TABLE question_picture
    ADD CONSTRAINT FK_QUESTION_PICTURE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE week_memory
    ADD CONSTRAINT FK_WEEK_MEMORY_ON_COUPLE FOREIGN KEY (couple_id) REFERENCES couple (id);

ALTER TABLE week_memory
    ADD CONSTRAINT FK_WEEK_MEMORY_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

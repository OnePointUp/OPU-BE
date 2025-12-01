
ALTER TABLE member
    ADD COLUMN web_push_agreed TINYINT(1) NOT NULL DEFAULT 0 AFTER is_email_verified,
    ADD COLUMN nickname_tag VARCHAR(10) NULL AFTER nickname;
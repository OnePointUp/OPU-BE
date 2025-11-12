
-- 알림 유형 테이블
CREATE TABLE IF NOT EXISTS notification_type (
                                                 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,

                                                 code VARCHAR(50) NOT NULL UNIQUE,    -- ALL, MORNING, EVENING, ROUTINE, RANDOM_PICK
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL,
    default_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    default_time TIME NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



-- 사용자 알림 설정 테이블
CREATE TABLE IF NOT EXISTS member_notification_setting (
                                                           id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                                           member_id BIGINT NOT NULL,
                                                           notification_type_id BIGINT NOT NULL,
                                                           enabled BOOLEAN NOT NULL DEFAULT TRUE,

                                                           updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,

                                                           CONSTRAINT fk_setting_member FOREIGN KEY (member_id)
    REFERENCES member(id) ON DELETE CASCADE,

    CONSTRAINT fk_setting_type FOREIGN KEY (notification_type_id)
    REFERENCES notification_type(id) ON DELETE CASCADE,

    UNIQUE KEY uk_member_notification_type (member_id, notification_type_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 알림 테이블
CREATE TABLE IF NOT EXISTS notification (
                                            id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                            member_id BIGINT NOT NULL,
                                            notification_type_id BIGINT NOT NULL,

                                            title VARCHAR(100) NOT NULL,
    message VARCHAR(255) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    linked_content_id BIGINT NULL,
    CONSTRAINT fk_notification_member
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,

    CONSTRAINT fk_notification_type
    FOREIGN KEY (notification_type_id) REFERENCES notification_type(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE INDEX idx_notification_member ON notification(member_id);
CREATE INDEX idx_notification_type ON notification(notification_type_id);
CREATE INDEX idx_notification_created ON notification(created_at);
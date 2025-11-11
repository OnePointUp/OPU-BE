
--  알림 기록 테이블
CREATE TABLE IF NOT EXISTS notification (
                                            id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                            member_id BIGINT NOT NULL,
                                            title VARCHAR(100) NOT NULL,
    message VARCHAR(255) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notification_member
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_notification_member ON notification(member_id);
CREATE INDEX idx_notification_read ON notification(is_read);
CREATE INDEX idx_notification_created ON notification(created_at);



--  사용자 알림 설정 테이블
CREATE TABLE IF NOT EXISTS member_notification_setting (
                                                           member_id BIGINT NOT NULL PRIMARY KEY,

                                                           all_notification_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                                           morning_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                                           evening_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                                           routine_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                                           todo_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                                           random_pick_enabled BOOLEAN NOT NULL DEFAULT TRUE,

                                                           updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,

                                                           CONSTRAINT fk_setting_member
                                                           FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
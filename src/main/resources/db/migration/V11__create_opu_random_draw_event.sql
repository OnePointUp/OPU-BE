-- OPU 랜덤 뽑기 이력 테이블 생성
CREATE TABLE IF NOT EXISTS opu_random_draw_event (
                                                     id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                                     member_id BIGINT NOT NULL,
                                                     opu_id BIGINT NULL,
                                                     source ENUM('ALL', 'FAVORITE') NOT NULL,
    required_minutes INT NULL,
    drawn_at DATETIME NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_draw_member
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT fk_draw_opu
    FOREIGN KEY (opu_id) REFERENCES opu(id) ON DELETE SET NULL,

    INDEX idx_draw_member_time (member_id, drawn_at)
    ) ENGINE=InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;
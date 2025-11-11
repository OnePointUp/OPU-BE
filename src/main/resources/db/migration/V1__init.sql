-- 사용자 테이블
CREATE TABLE IF NOT EXISTS member(
                                     id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                     email VARCHAR(255) NULL,
                                     password VARCHAR(255) NULL,
                                     nickname VARCHAR(50) NOT NULL,
                                     profile_image_url VARCHAR(512) NULL,
                                     bio VARCHAR(200) NULL,
                                     authorization ENUM('MEMBER','ADMIN') NOT NULL DEFAULT 'MEMBER',
                                     auth_provider VARCHAR(32) NOT NULL DEFAULT 'local',
                                     provider_id VARCHAR(255) NULL,
                                     is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
                                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
                                     deleted_at TIMESTAMP NULL,
                                     last_login TIMESTAMP NULL,
                                     UNIQUE KEY uk_provider (auth_provider, provider_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- OPU 카테고리 테이블
CREATE TABLE IF NOT EXISTS opu_category (
                                            id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                            name VARCHAR(50) NOT NULL,
                                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
                                            UNIQUE KEY uk_category_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- OPU 테이블
CREATE TABLE IF NOT EXISTS opu (
                                   id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                   member_id BIGINT NULL,
                                   category_id BIGINT NULL,
                                   title VARCHAR(100) NOT NULL,
                                   description VARCHAR(255) NULL,
                                   required_minutes INT NULL,
                                   is_shared BOOLEAN NOT NULL DEFAULT FALSE,
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
                                   deleted_at TIMESTAMP NULL,
                                   CONSTRAINT fk_opu_creator FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL,
                                   CONSTRAINT fk_opu_category FOREIGN KEY (category_id) REFERENCES opu_category(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 (조회패턴에 맞게)
CREATE INDEX idx_opu_creator ON opu(member_id);
CREATE INDEX idx_opu_category ON opu(category_id);
CREATE INDEX idx_opu_shared_created ON opu(is_shared, created_at DESC);
CREATE INDEX idx_opu_cat_shared_created ON opu(category_id, is_shared, created_at DESC);
CREATE INDEX idx_opu_member_created ON opu(member_id, created_at DESC);



-- 사용자 OPU 수행 테이블
CREATE TABLE member_opu_event (
                                  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                  member_id BIGINT NOT NULL,
                                  opu_id BIGINT NOT NULL,
                                  completed_at DATETIME NOT NULL,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_event_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                                  CONSTRAINT fk_event_opu FOREIGN KEY (opu_id) REFERENCES opu(id) ON DELETE CASCADE,

                                  INDEX idx_event_member_time (member_id, completed_at),
                                  INDEX idx_event_opu_time    (opu_id, completed_at),
                                  INDEX idx_event_time        (completed_at),
                                  INDEX idx_event_member_opu_time (member_id, opu_id, completed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 사용자 OPU 완료 횟수 카운터 테이블
CREATE TABLE member_opu_counter (
                                    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                    member_id BIGINT NOT NULL,
                                    opu_id BIGINT NOT NULL,
                                    total_completions INT NOT NULL DEFAULT 0,
                                    last_completed_at DATETIME NULL,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,

                                    CONSTRAINT fk_counter_member FOREIGN KEY (member_id)
                                        REFERENCES member(id) ON DELETE CASCADE,
                                    CONSTRAINT fk_counter_opu FOREIGN KEY (opu_id)
                                        REFERENCES opu(id) ON DELETE CASCADE,

                                    UNIQUE KEY uk_member_opu (member_id, opu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 찜한 OPU 테이블
CREATE TABLE IF NOT EXISTS favorite_opu (
                                            id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                            member_id BIGINT NOT NULL,
                                            opu_id BIGINT NOT NULL,
                                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                            CONSTRAINT fk_fav_member FOREIGN KEY (member_id)
                                                REFERENCES member(id) ON DELETE CASCADE,
                                            CONSTRAINT fk_fav_opu FOREIGN KEY (opu_id)
                                                REFERENCES opu(id) ON DELETE CASCADE,

                                            UNIQUE KEY uk_favorite (member_id, opu_id),         -- 중복 찜 방지
                                            INDEX idx_favorite_member (member_id),              -- 내 찜 목록 조회
                                            INDEX idx_favorite_opu (opu_id)                     -- 특정 OPU가 얼마나 찜되었는지 조회 가능
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 차단 OPU 테이블
CREATE TABLE IF NOT EXISTS blocked_opu (
                                           id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                           member_id BIGINT NOT NULL,
                                           opu_id BIGINT NOT NULL,
                                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                           CONSTRAINT fk_block_member FOREIGN KEY (member_id)
                                               REFERENCES member(id) ON DELETE CASCADE,
                                           CONSTRAINT fk_block_opu FOREIGN KEY (opu_id)
                                               REFERENCES opu(id) ON DELETE CASCADE,

                                           UNIQUE KEY uk_block (member_id, opu_id),           -- 중복 차단 방지
                                           INDEX idx_block_member (member_id),
                                           INDEX idx_block_opu (opu_id) -- 추후 차단이 많이된 OPU에 대한 조치 필요한 경우
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 루틴 테이블
CREATE TABLE IF NOT EXISTS routine (
                                       id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                       member_id BIGINT NOT NULL,               -- 루틴 생성 사용자
                                       title VARCHAR(100) NOT NULL,
                                       frequency ENUM('DAILY', 'WEEKLY', 'BIWEEKLY', 'MONTHLY', 'YEARLY') NOT NULL,
                                       start_date DATE NOT NULL,
                                       end_date DATE NULL,
                                       alarm_time TIME NULL,                    -- 알림이 있을 경우
                                       is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,

                                       CONSTRAINT fk_routine_member
                                           FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 루틴 스케쥴 테이블
CREATE TABLE IF NOT EXISTS routine_schedule (
                                                id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                                routine_id BIGINT NOT NULL,
                                                week_days SET('MON','TUE','WED','THU','FRI','SAT','SUN') NULL,
                                                month_days TEXT NULL,   -- 예: JSON 문자열 '[5,10,25]'을 저장
                                                days TEXT NULL,   -- 예: JSON 문자열 '[{"month":3,"day":1},{"month":9,"day":15}]'
                                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                CONSTRAINT fk_schedule_routine FOREIGN KEY (routine_id) REFERENCES routine(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 투두 테이블
CREATE TABLE IF NOT EXISTS todo (
                                    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                    member_id BIGINT NOT NULL,
                                    routine_id BIGINT NULL,                   -- 루틴에서 생성된 경우
                                    opu_id BIGINT NULL,                       -- OPU에서 생성된 경우

                                    title VARCHAR(100) NOT NULL,
                                    scheduled_date DATE NULL,     -- 수행 예정 날짜
                                    scheduled_time TIME NULL,     -- 수행 예정 시간

                                    is_completed BOOLEAN NOT NULL DEFAULT FALSE,

                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,

                                    CONSTRAINT fk_todo_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                                    CONSTRAINT fk_todo_routine FOREIGN KEY (routine_id) REFERENCES routine(id) ON DELETE SET NULL,
                                    CONSTRAINT fk_todo_opu FOREIGN KEY (opu_id) REFERENCES opu(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 조회 최적화를 위한 인덱스
CREATE INDEX idx_todo_member_scheduled ON todo(member_id, scheduled_date);
CREATE INDEX idx_todo_routine ON todo(routine_id);
CREATE INDEX idx_todo_opu ON todo(opu_id);


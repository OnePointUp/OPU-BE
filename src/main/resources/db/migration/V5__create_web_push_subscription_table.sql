CREATE TABLE IF NOT EXISTS web_push_subscription (
                                                     id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,

                                                     member_id BIGINT NOT NULL,

                                                     endpoint VARCHAR(500) NOT NULL,
    p256dh VARCHAR(255) NOT NULL,
    auth VARCHAR(255) NOT NULL,

    expiration_time TIMESTAMP NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_member_endpoint (member_id, endpoint),

    CONSTRAINT fk_subscription_member FOREIGN KEY (member_id)
    REFERENCES member(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
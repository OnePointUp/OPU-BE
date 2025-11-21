
ALTER TABLE member
    ADD COLUMN email_verify_issued_at DATETIME NULL,
    ADD COLUMN password_reset_issued_at DATETIME NULL;
CREATE TABLE member_device (
    member_device_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    fcm_token VARCHAR(255) NOT NULL,
    device_type VARCHAR(20) NOT NULL,
    device_identifier VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT FK_MEMBER_DEVICE_MEMBER FOREIGN KEY (member_id) REFERENCES member(member_id),
    CONSTRAINT UK_MEMBER_DEVICE UNIQUE (member_id, device_identifier)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
-- Posts table
CREATE TABLE post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    user_full_name VARCHAR(255) NOT NULL,
    user_profile_photo VARCHAR(500),
    image_url VARCHAR(500) NOT NULL,
    caption TEXT,
    likes_count BIGINT NOT NULL DEFAULT 0,
    comments_count BIGINT NOT NULL DEFAULT 0,
    location VARCHAR(255) NOT NULL DEFAULT '',
    is_liked_by_current_user BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
);

-- Comments table
CREATE TABLE comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    user_full_name VARCHAR(255) NOT NULL,
    user_profile_photo VARCHAR(500),
    content TEXT NOT NULL,
    likes_count BIGINT NOT NULL DEFAULT 0,
    is_liked_by_current_user BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
);

-- Likes table
CREATE TABLE like_entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    user_full_name VARCHAR(255) NOT NULL,
    user_profile_photo VARCHAR(500),
    target_id BIGINT NOT NULL,
    target_type VARCHAR(50) NOT NULL, -- "POST" or "COMMENT"
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_like (user_id, target_id, target_type),
    INDEX idx_target (target_id, target_type),
    INDEX idx_user_id (user_id)
);

-- Notifications table
CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    sender_user_name VARCHAR(255) NOT NULL,
    sender_full_name VARCHAR(255) NOT NULL,
    sender_profile_photo VARCHAR(500),
    type VARCHAR(50) NOT NULL, -- "LIKE", "COMMENT", "FOLLOW", "MENTION"
    message VARCHAR(500) NOT NULL,
    target_id BIGINT NOT NULL,
    target_type VARCHAR(50) NOT NULL, -- "POST", "COMMENT", "USER"
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    target_url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_recipient_id (recipient_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
); 
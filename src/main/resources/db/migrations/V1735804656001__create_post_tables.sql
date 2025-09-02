-- Posts table
CREATE TABLE post (
    id BIGSERIAL PRIMARY KEY,
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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Comments table
CREATE TABLE comment (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    user_full_name VARCHAR(255) NOT NULL,
    user_profile_photo VARCHAR(500),
    content TEXT NOT NULL,
    likes_count BIGINT NOT NULL DEFAULT 0,
    is_liked_by_current_user BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Likes table
CREATE TABLE like_entity (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    user_full_name VARCHAR(255) NOT NULL,
    user_profile_photo VARCHAR(500),
    target_id BIGINT NOT NULL,
    target_type VARCHAR(50) NOT NULL, -- "POST" or "COMMENT"
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Notifications table
CREATE TABLE notification (
    id BIGSERIAL PRIMARY KEY,
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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_post_user_id ON post(user_id);
CREATE INDEX idx_post_created_at ON post(created_at);

CREATE INDEX idx_comment_post_id ON comment(post_id);
CREATE INDEX idx_comment_user_id ON comment(user_id);
CREATE INDEX idx_comment_created_at ON comment(created_at);

CREATE UNIQUE INDEX unique_like ON like_entity(user_id, target_id, target_type);
CREATE INDEX idx_like_target ON like_entity(target_id, target_type);
CREATE INDEX idx_like_user_id ON like_entity(user_id);

CREATE INDEX idx_notification_recipient_id ON notification(recipient_id);
CREATE INDEX idx_notification_sender_id ON notification(sender_id);
CREATE INDEX idx_notification_is_read ON notification(is_read);
CREATE INDEX idx_notification_created_at ON notification(created_at); 
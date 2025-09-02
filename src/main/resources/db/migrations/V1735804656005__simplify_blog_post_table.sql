-- Drop existing blog_post table and recreate with simplified structure
DROP TABLE IF EXISTS blog_post CASCADE;

-- Create simplified blog_post table
CREATE TABLE blog_post (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    attachments TEXT, -- JSON array of attachment URLs
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index for faster lookups
CREATE INDEX idx_blog_post_created_by ON blog_post(created_by);
CREATE INDEX idx_blog_post_created_at ON blog_post(created_at);
CREATE INDEX idx_blog_post_title_website ON blog_post(title, created_by);

-- Create system user table
CREATE TABLE system_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    is_system_user BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert default system user (ID: 1)
INSERT INTO system_user (id, username, email, full_name, is_system_user) VALUES
(1, 'system', 'system@blog-platform.com', 'System User', true);

-- Set the sequence to start from 2 for future users
SELECT setval('system_user_id_seq', 1, true);

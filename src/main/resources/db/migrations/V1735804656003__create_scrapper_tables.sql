-- Website table for storing websites to scrape
CREATE TABLE website (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(500) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_scraped BOOLEAN NOT NULL DEFAULT FALSE,
    last_scraped_at VARCHAR(50),
    total_posts_scraped INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Blog Post table for storing scraped blog posts and user posts
CREATE TABLE blog_post (
    id BIGSERIAL PRIMARY KEY,
    website_id BIGINT,
    website_url VARCHAR(500),
    user_id BIGINT,
    blog_title VARCHAR(500) NOT NULL,
    original_link VARCHAR(500) UNIQUE,
    author VARCHAR(255),
    publish_date VARCHAR(100),
    read_time VARCHAR(50),
    excerpt TEXT,
    featured_image VARCHAR(500),
    content TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_scraped BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_website_url ON website(url);
CREATE INDEX idx_website_is_active ON website(is_active);
CREATE INDEX idx_website_is_scraped ON website(is_scraped);

CREATE INDEX idx_blog_post_website_id ON blog_post(website_id);
CREATE INDEX idx_blog_post_user_id ON blog_post(user_id);
CREATE INDEX idx_blog_post_original_link ON blog_post(original_link);
CREATE INDEX idx_blog_post_is_active ON blog_post(is_active);
CREATE INDEX idx_blog_post_created_at ON blog_post(created_at);
CREATE INDEX idx_blog_post_is_scraped ON blog_post(is_scraped);

-- Add foreign key constraints
ALTER TABLE blog_post ADD CONSTRAINT fk_blog_post_website 
    FOREIGN KEY (website_id) REFERENCES website(id) ON DELETE SET NULL;

-- Insert some default websites
INSERT INTO website (url, name, description) VALUES 
('https://medium.com/blog/all', 'Medium Blog', 'Official Medium blog with latest stories'),
('https://medium.com/@shivambhadani_', 'Shivam Bhadani', 'Personal blog of Shivam Bhadani on Medium');

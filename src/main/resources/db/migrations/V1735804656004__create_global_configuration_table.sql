-- Create global configuration table
CREATE TABLE global_configuration (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(255) NOT NULL UNIQUE,
    config_value TEXT,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert default configurations
INSERT INTO global_configuration (config_key, config_value, description) VALUES
('s3_enabled', 'false', 'Enable/disable S3 file storage'),
('s3_bucket_name', 'blog-platform-images', 'S3 bucket name for file storage'),
('s3_region', 'us-east-1', 'AWS S3 region'),
('s3_access_key', '', 'AWS S3 access key'),
('s3_secret_key', '', 'AWS S3 secret key'),
('local_storage_path', '/app/uploads', 'Local file storage path'),
('max_file_size_mb', '10', 'Maximum file size in MB'),
('allowed_file_types', 'jpg,jpeg,png,gif', 'Comma-separated list of allowed file types'),
('file_upload_enabled', 'true', 'Enable/disable file upload functionality'),
('image_compression_enabled', 'true', 'Enable/disable image compression'),
('image_compression_quality', '80', 'Image compression quality (1-100)');

-- Create index for faster lookups
CREATE INDEX idx_global_config_key ON global_configuration(config_key);
CREATE INDEX idx_global_config_active ON global_configuration(is_active);

CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY, 
    full_name VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_profile (
    id BIGSERIAL PRIMARY KEY, 
    user_id BIGINT,
    followers BIGINT,
    followings BIGINT,
    bio VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_connection (
    id BIGSERIAL PRIMARY KEY, 
    user_id BIGINT,
    photo VARCHAR(255),
    full_name VARCHAR(255),
    user_name VARCHAR(255),
    follower_id BIGINT,
    follower_photo VARCHAR(255),
    follower_full_name VARCHAR(255),
    follower_user_name VARCHAR(255),
    is_following boolean DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

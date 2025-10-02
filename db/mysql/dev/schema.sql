-- =====================================================
-- Memento Application - Complete MySQL DDL Schema
-- Environment: Development
-- Character Set: UTF8MB4 for full Unicode support
-- =====================================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE memento;

-- =====================================================
-- Core User and Community Tables
-- =====================================================

CREATE TABLE IF NOT EXISTS members (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(102) NOT NULL,
    email VARCHAR(255) NOT NULL,
    birthday DATE NULL,
    kakao_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_members_kakao_id (kakao_id),
    KEY idx_members_email (email),
    KEY idx_members_active (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS communities (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(102) NOT NULL,
    member_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    KEY fk_communities_member (member_id),
    KEY idx_communities_name (name),
    KEY idx_communities_active (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Achievement System
-- =====================================================

CREATE TABLE IF NOT EXISTS achievements (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(102) NOT NULL,
    criteria VARCHAR(255) NOT NULL,
    type ENUM('OPEN', 'RESTRICTED', 'HIDDEN') NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    KEY idx_achievements_type (type),
    KEY idx_achievements_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Associate (Member-Community Relationships)
-- =====================================================

CREATE TABLE IF NOT EXISTS associates (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nickname VARCHAR(51) NOT NULL,
    profile_image_url VARCHAR(255) NULL,
    introduction VARCHAR(255) NULL,
    achievement_id BIGINT NULL,
    member_id BIGINT NOT NULL,
    community_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_associates_member_community (member_id, community_id),
    KEY fk_associates_achievement (achievement_id),
    KEY fk_associates_member (member_id),
    KEY fk_associates_community (community_id),
    KEY idx_associates_nickname (nickname),
    KEY idx_associates_member_community (member_id, community_id),
    KEY idx_associates_active (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS associate_stats (
    id BIGINT NOT NULL AUTO_INCREMENT,
    associate_id BIGINT NOT NULL,
    consecutive_attendance_days INT NOT NULL DEFAULT 0,
    last_attended_at DATETIME(6) NULL,
    uploaded_reaction_count INT NOT NULL DEFAULT 0,
    used_reaction_count INT NOT NULL DEFAULT 0,
    guest_book_count INT NOT NULL DEFAULT 0,
    uploaded_profile_image_count INT NOT NULL DEFAULT 0,
    registered_profile_image_count INT NOT NULL DEFAULT 0,
    uploaded_post_image_count INT NOT NULL DEFAULT 0,
    created_memory_count INT NOT NULL DEFAULT 0,
    joined_memory_count INT NOT NULL DEFAULT 0,
    mbti_test_count INT NOT NULL DEFAULT 0,
    f_mbti_count INT NOT NULL DEFAULT 0,
    t_mbti_count INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_associate_stats_associate (associate_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS achievement_associate (
    id BIGINT NOT NULL AUTO_INCREMENT,
    achievement_id BIGINT NOT NULL,
    associate_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_achievement_associate (achievement_id, associate_id),
    KEY fk_achievement_associate_achievement (achievement_id),
    KEY fk_achievement_associate_associate (associate_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Events and Memories
-- =====================================================

CREATE TABLE IF NOT EXISTS events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(102) NOT NULL,
    description VARCHAR(510) NOT NULL,
    -- Location embedded fields
    latitude DECIMAL(10,8) NULL,
    longitude DECIMAL(11,8) NULL,
    code INT NULL,
    name VARCHAR(102) NULL,
    address VARCHAR(255) NULL,
    -- Period embedded fields  
    start_time DATETIME(6) NULL,
    end_time DATETIME(6) NULL,
    community_id BIGINT NOT NULL,
    associate_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    KEY fk_events_community (community_id),
    KEY fk_events_associate (associate_id),
    KEY idx_events_title (title),
    KEY idx_events_start_time (start_time),
    KEY idx_events_location (latitude, longitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS memories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    event_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_memories_event (event_id),
    KEY idx_memories_active (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS memory_associate (
    id BIGINT NOT NULL AUTO_INCREMENT,
    memory_id BIGINT NOT NULL,
    associate_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_memory_associate (memory_id, associate_id),
    KEY fk_memory_associate_memory (memory_id),
    KEY fk_memory_associate_associate (associate_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Posts and Media
-- =====================================================

CREATE TABLE IF NOT EXISTS posts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content VARCHAR(510) NOT NULL,
    memory_id BIGINT NOT NULL,
    associate_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    KEY fk_posts_memory (memory_id),
    KEY fk_posts_associate (associate_id),
    KEY idx_posts_created_at (created_at),
    KEY idx_posts_memory_created (memory_id, created_at DESC),
    KEY idx_posts_active (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS post_images (
    id BIGINT NOT NULL AUTO_INCREMENT,
    url VARCHAR(255) NOT NULL,
    hash VARCHAR(255) NOT NULL,
    post_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    KEY fk_post_images_post (post_id),
    KEY idx_post_images_hash (hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Comments and Reactions
-- =====================================================

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    url VARCHAR(255) NOT NULL,
    type ENUM('EMOJI', 'VOICE') NOT NULL,
    post_id BIGINT NOT NULL,
    associate_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    KEY fk_comments_post (post_id),
    KEY fk_comments_associate (associate_id),
    KEY idx_comments_type (type),
    KEY idx_comments_post_type (post_id, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS voices (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(102) NULL,
    url VARCHAR(255) NOT NULL,
    temporary BOOLEAN NOT NULL DEFAULT TRUE,
    associate_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_voices_name (name),
    KEY fk_voices_associate (associate_id),
    KEY idx_voices_temporary (temporary)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS emoji (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(102) NOT NULL,
    url VARCHAR(255) NOT NULL,
    associate_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_emoji_name (name),
    KEY fk_emoji_associate (associate_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Profile Images
-- =====================================================

CREATE TABLE IF NOT EXISTS profile_images (
    id BIGINT NOT NULL AUTO_INCREMENT,
    url VARCHAR(255) NOT NULL,
    associate_id BIGINT NOT NULL,
    registrant_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    KEY fk_profile_images_associate (associate_id),
    KEY fk_profile_images_registrant (registrant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Communication Features
-- =====================================================

CREATE TABLE IF NOT EXISTS guest_books (
    id BIGINT NOT NULL AUTO_INCREMENT,
    type ENUM('TEXT', 'EMOJI', 'VOICE') NOT NULL,
    content VARCHAR(510) NOT NULL,
    name VARCHAR(102) NULL,
    associate_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    KEY fk_guest_books_associate (associate_id),
    KEY idx_guest_books_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(102) NOT NULL,
    content VARCHAR(255) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    type ENUM('MEMORY', 'REACTION', 'POST', 'ACHIEVE', 'GUESTBOOK', 'MBTI', 'NEWIMAGE', 'BIRTHDAY', 'ASSOCIATE') NOT NULL,
    actor_id BIGINT NULL,
    post_id BIGINT NULL,
    memory_id BIGINT NULL,
    receiver_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    KEY fk_notifications_receiver (receiver_id),
    KEY idx_notifications_type (type),
    KEY idx_notifications_is_read (is_read),
    KEY idx_notifications_created_at (created_at),
    KEY idx_notifications_receiver_read (receiver_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- MBTI Testing System
-- =====================================================

CREATE TABLE IF NOT EXISTS mbti_test (
    id BIGINT NOT NULL AUTO_INCREMENT,
    mbti ENUM('ISFP', 'ISFJ', 'ISTP', 'ISTJ', 'INFP', 'INFJ', 'INTP', 'INTJ', 
              'ESFP', 'ESFJ', 'ESTP', 'ESTJ', 'ENFP', 'ENFJ', 'ENTP', 'ENTJ') NOT NULL,
    from_associate_id BIGINT NOT NULL,
    to_associate_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    KEY fk_mbti_test_from_associate (from_associate_id),
    KEY fk_mbti_test_to_associate (to_associate_id),
    KEY idx_mbti_test_mbti (mbti),
    KEY idx_mbti_test_associates (from_associate_id, to_associate_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Views for Common Queries
-- =====================================================

-- Active members view (excluding soft-deleted)
CREATE OR REPLACE VIEW active_members AS 
SELECT * FROM members WHERE deleted_at IS NULL;

-- Active associates with community and member info
CREATE OR REPLACE VIEW active_associates_with_details AS 
SELECT 
    a.id,
    a.nickname,
    a.profile_image_url,
    a.introduction,
    m.name as member_name,
    m.email as member_email,
    c.name as community_name,
    a.created_at,
    a.modified_at
FROM associates a
JOIN members m ON a.member_id = m.id
JOIN communities c ON a.community_id = c.id
WHERE a.deleted_at IS NULL 
  AND m.deleted_at IS NULL 
  AND c.deleted_at IS NULL;
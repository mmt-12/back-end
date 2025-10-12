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

CREATE TABLE IF NOT EXISTS fcm_tokens (
  id BIGINT NOT NULL AUTO_INCREMENT,
  token VARCHAR(512) NOT NULL,
  associate_id BIGINT NOT NULL,
  created_at DATETIME(6) NOT NULL,
  modified_at DATETIME(6) NULL,
  deleted_at DATETIME(6) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_fcm_tokens_token (token),
  KEY fk_fcm_tokens_associate (associate_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO achievements (id, name, criteria, `type`, created_at, modified_at, deleted_at)
VALUES
(1, '시간빌게이츠', '“또 오셨네요?” 연속 출석 달성', 'OPEN', NOW(6), NULL, NULL),
(2, '관상가', '“MBTI 분석 마스터” MBTI 테스트를 성실히 하면 획득', 'OPEN', NOW(6), NULL, NULL),
(3, '다중인격', '“지금도 당신의 MBTI는 바뀌는 중” 여러 종류의 MBTI를 수집', 'OPEN', NOW(6), NULL, NULL),
(4, 'FFFFFF', '“감성 MAX, F의 끝판왕” F가 들어간 모든 MBTI를 수집', 'OPEN', NOW(6), NULL, NULL),
(5, 'T발 C야?', '“이성 MAX, T의 끝판왕” T가 들어간 모든 MBTI를 수집', 'OPEN', NOW(6), NULL, NULL),
(6, '리액션공장', '“오늘도 돌아가는 리액션 공장” 리액션을 많이 등록하면 획득', 'OPEN', NOW(6), NULL, NULL),
(7, '입에서주스가주르륵', '“리액션 없으면 대화 불가” 댓글 작성 횟수가 많으면 획득', 'OPEN', NOW(6), NULL, NULL),
(8, '변검술사', '“오늘은 어떤 얼굴을 쓸까” 등록된 프로필 이미지가 많으면 획득', 'OPEN', NOW(6), NULL, NULL),
(9, '파파라치', '“사진 찍기 좋아하는 당신은” 등록한 프로필 이미지가 많으면 획득', 'OPEN', NOW(6), NULL, NULL),
(10, '전문찍새', '“여행을 가면 사진을 찍어야지” 포스트 이미지를 많이 업로드', 'OPEN', NOW(6), NULL, NULL),
(11, '마니또', '“너 혹시 내 마니또야?” 작성한 방명록이 많으면 획득', 'OPEN', NOW(6), NULL, NULL),
(12, '민들레? 노브랜드?', '“이번엔 새로운데 갈거지?” 생성한 기억이 많으면 획득', 'OPEN', NOW(6), NULL, NULL),
(13, 'GMG', '“여기도 갔어?” 참여한 기억이 많으면 획득', 'OPEN', NOW(6), NULL, NULL),
(14, '업적헌터#kill', '“모든 업적을 사냥” 공개된 모든 업적을 달성', 'OPEN', NOW(6), NULL, NULL),
(15, '홈 스윗 홈', '“우리가 태어난 그리운 그 곳” 지도에서 특정 위치를 찾기', 'OPEN', NOW(6), NULL, NULL),
(16, '13일의 금요일', '"이 으스스한 날에 무슨일로..." 이스터에그', 'OPEN', NOW(6), NULL, NULL),
(17, '씽씽씽', '오준수 曰 “씽씽씽”', 'OPEN', NOW(6), NULL, NULL),
(18, '팅팅팅', '오준수 曰 “팅팅팅”', 'OPEN', NOW(6), NULL, NULL),
(19, '쿠로네코', '12반의 검은고양이', 'OPEN', NOW(6), NULL, NULL),
(20, '횬딘곤듀', '12반 영원한 공주', 'OPEN', NOW(6), NULL, NULL),
(21, '귀한곳에누추한분이', '어…왔어?', 'OPEN', NOW(6), NULL, NULL),
(22, '뤼전드', '진짜 넌…', 'OPEN', NOW(6), NULL, NULL),
(23, '주피티', '알려줘 주빈아', 'OPEN', NOW(6), NULL, NULL),
(24, '신', '그저 갓 도영', 'OPEN', NOW(6), NULL, NULL),
(25, '그녀석', '와 대산이다', 'OPEN', NOW(6), NULL, NULL),
(26, '인형', '인형 뽑기 마스터', 'OPEN', NOW(6), NULL, NULL),
(27, '닥치', '쉿', 'OPEN', NOW(6), NULL, NULL),
(28, 'ㅁㅇㅁㅇ', '이거 이렇게 될 줄 모르고 만들었는데, 이게 되네', 'OPEN', NOW(6), NULL, NULL),
(29, '내절친', '그들이 진짜 절친이라는 것을 증명해 준다', 'OPEN', NOW(6), NULL, NULL),
(30, 'GAY', '그들만의 끈끈하고 비밀스러운 우정을 증명해 준다', 'OPEN', NOW(6), NULL, NULL),
(31, '드디어봐주는구나', '연락 기다리고 있었어', 'OPEN', NOW(6), NULL, NULL),
(32, '현지', '현지야', 'OPEN', NOW(6), NULL, NULL),
(33, '랑이와싹이', '여기서 이러지 말고 밖에 나가서 놀아', 'OPEN', NOW(6), NULL, NULL);
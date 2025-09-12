CREATE DATABASE IF NOT EXISTS memento CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE memento;

INSERT INTO members (id, name, email, birthday, kakao_id, created_at, modified_at, deleted_at)
VALUES (1, 'default_member', 'default@example.com', NULL, 10001, NOW(6), NULL, NULL);

INSERT INTO communities (id, name, member_id, created_at, modified_at, deleted_at)
VALUES (1, 'ssafy12', 1, NOW(6), NULL, NULL);

-- Members
INSERT INTO members (id, name, email, birthday, kakao_id, created_at, modified_at, deleted_at)
VALUES 
(2, 'Alice', 'alice@example.com', '1998-05-12', 10002, NOW(6), NULL, NULL),
(3, 'Bob', 'bob@example.com', '1997-11-23', 10003, NOW(6), NULL, NULL),
(4, 'Charlie', 'charlie@example.com', '1999-02-08', 10004, NOW(6), NULL, NULL);

-- Associates (link members to community "ssafy12")
INSERT INTO associates (id, nickname, profile_image_url, introduction, achievement_id, member_id, community_id, created_at, modified_at, deleted_at)
VALUES
(1, 'AliceNick', NULL, 'Hi, I am Alice!', NULL, 2, 1, NOW(6), NULL, NULL),
(2, 'BobNick', NULL, 'Hey, I am Bob.', NULL, 3, 1, NOW(6), NULL, NULL),
(3, 'CharlieNick', NULL, 'Hello, I am Charlie.', NULL, 4, 1, NOW(6), NULL, NULL);

-- Initialize associate_stats for each associate
INSERT INTO associate_stats (id, associate_id, consecutive_attendance_days, last_attended_at, uploaded_reaction_count, used_reaction_count, guest_book_count, uploaded_profile_image_count, registered_profile_image_count, uploaded_post_image_count, created_memory_count, joined_memory_count, mbti_test_count, f_mbti_count, t_mbti_count)
VALUES
(1, 1, 0, NULL, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(2, 2, 0, NULL, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(3, 3, 0, NULL, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

INSERT INTO achievements (id, name, criteria, type, created_at, modified_at, deleted_at)
VALUES
(1, '시간빌게이츠', 'Earn the 시간빌게이츠 badge', 'OPEN', NOW(6), NULL, NULL),
(2, '관상가', 'Earn the 관상가 badge', 'OPEN', NOW(6), NULL, NULL),
(3, '다중인격', 'Earn the 다중인격 badge', 'OPEN', NOW(6), NULL, NULL),
(4, 'FFFFFF', 'Earn the FFFFFF badge', 'OPEN', NOW(6), NULL, NULL),
(5, 'T발 C야?', 'Earn the T발 C야? badge', 'OPEN', NOW(6), NULL, NULL),
(6, '리액션공장', 'Earn the 리액션공장 badge', 'OPEN', NOW(6), NULL, NULL),
(7, '입에서주스가주르륵', 'Earn the 입에서주스가주르륵 badge', 'OPEN', NOW(6), NULL, NULL),
(8, '변검술사', 'Earn the 변검술사 badge', 'OPEN', NOW(6), NULL, NULL),
(9, '파파라치', 'Earn the 파파라치 badge', 'OPEN', NOW(6), NULL, NULL),
(10, '전문찍새', 'Earn the 전문찍새 badge', 'OPEN', NOW(6), NULL, NULL),
(11, '마니또', 'Earn the 마니또 badge', 'OPEN', NOW(6), NULL, NULL),
(12, '민들레? 노브랜드?', 'Earn the 민들레? 노브랜드? badge', 'OPEN', NOW(6), NULL, NULL),
(13, 'GMG', 'Earn the GMG badge', 'OPEN', NOW(6), NULL, NULL),
(14, '업적헌터#kill', 'Earn the 업적헌터#kill badge', 'OPEN', NOW(6), NULL, NULL),
(15, '홈 스윗 홈', 'Earn the 홈 스윗 홈 badge', 'OPEN', NOW(6), NULL, NULL),
(16, '13일의 금요일', 'Earn the 13일의 금요일 badge', 'OPEN', NOW(6), NULL, NULL),
(17, '씽씽씽', 'Earn the 씽씽씽 badge', 'OPEN', NOW(6), NULL, NULL),
(18, '팅팅팅', 'Earn the 팅팅팅 badge', 'OPEN', NOW(6), NULL, NULL),
(19, '쿠로네코', 'Earn the 쿠로네코 badge', 'OPEN', NOW(6), NULL, NULL),
(20, '횬딘곤듀', 'Earn the 횬딘곤듀 badge', 'OPEN', NOW(6), NULL, NULL),
(21, '귀한곳에누추한분이', 'Earn the 귀한곳에누추한분이 badge', 'OPEN', NOW(6), NULL, NULL),
(22, '뤼전드', 'Earn the 뤼전드 badge', 'OPEN', NOW(6), NULL, NULL),
(23, '주피티', 'Earn the 주피티 badge', 'OPEN', NOW(6), NULL, NULL),
(24, '신', 'Earn the 신 badge', 'OPEN', NOW(6), NULL, NULL),
(25, '그녀석', 'Earn the 그녀석 badge', 'OPEN', NOW(6), NULL, NULL),
(26, '인형', 'Earn the 인형 badge', 'OPEN', NOW(6), NULL, NULL),
(27, '닥치', 'Earn the 닥치 badge', 'OPEN', NOW(6), NULL, NULL),
(28, 'ㅁㅇㅁㅇ', 'Earn the ㅁㅇㅁㅇ badge', 'OPEN', NOW(6), NULL, NULL),
(29, '내절친', 'Earn the 내절친 badge', 'OPEN', NOW(6), NULL, NULL),
(30, 'GAY', 'Earn the GAY badge', 'OPEN', NOW(6), NULL, NULL),
(31, '드디어봐주는구나', 'Earn the 드디어봐주는구나 badge', 'OPEN', NOW(6), NULL, NULL),
(32, '현지', 'Earn the 현지 badge', 'OPEN', NOW(6), NULL, NULL),
(33, '랑이와싹이', 'Earn the 랑이와싹이 badge', 'OPEN', NOW(6), NULL, NULL);

-- Event: 양평 엠티
INSERT INTO events (id, title, description, latitude, longitude, code, name, address, start_time, end_time, community_id, associate_id, created_at, modified_at, deleted_at)
VALUES (1, '양평 엠티', '2023년 양평에서의 멋진 MT', 37.4929835, 127.5030058, 0, '경기도 양평시 양평군', '경기도 양평시 양평군',
        '2025-08-04 00:00:00', '2025-08-08 23:59:59', 1, 1, NOW(6), NULL, NULL);

INSERT INTO memories (id, event_id, created_at, modified_at, deleted_at)
VALUES (1, 1, NOW(6), NULL, NULL);

INSERT INTO memory_associate (id, memory_id, associate_id, created_at, modified_at, deleted_at)
VALUES 
(1, 1, 1, NOW(6), NULL, NULL),
(2, 1, 2, NOW(6), NULL, NULL),
(3, 1, 3, NOW(6), NULL, NULL);

-- Event: 강남 치킨
INSERT INTO events (id, title, description, latitude, longitude, code, name, address, start_time, end_time, community_id, associate_id, created_at, modified_at, deleted_at)
VALUES (2, '강남 치킨', '강남에서의 치킨 파티', 37.4973576, 127.0283168, 0, '감탄계 숯불치킨 강남역점', '서울특별시 강남구 강남대로',
        '2025-08-01 00:00:00', '2025-08-01 23:59:59', 1, 2, NOW(6), NULL, NULL);

INSERT INTO memories (id, event_id, created_at, modified_at, deleted_at)
VALUES (2, 2, NOW(6), NULL, NULL);

INSERT INTO memory_associate (id, memory_id, associate_id, created_at, modified_at, deleted_at)
VALUES 
(4, 2, 1, NOW(6), NULL, NULL),
(5, 2, 2, NOW(6), NULL, NULL),
(6, 2, 3, NOW(6), NULL, NULL);

-- Event: 양봉장 인터뷰
INSERT INTO events (id, title, description, latitude, longitude, code, name, address, start_time, end_time, community_id, associate_id, created_at, modified_at, deleted_at)
VALUES (3, '양봉장 인터뷰', '양봉장 인터뷰', 37.5619621, 127.3121992, 0, '남양주 리얼비보이', '경기도 남양주시 조안면',
        '2025-06-01 00:00:00', '2025-06-01 23:59:59', 1, 3, NOW(6), NULL, NULL);

INSERT INTO memories (id, event_id, created_at, modified_at, deleted_at)
VALUES (3, 3, NOW(6), NULL, NULL);

INSERT INTO memory_associate (id, memory_id, associate_id, created_at, modified_at, deleted_at)
VALUES 
(7, 3, 1, NOW(6), NULL, NULL),
(8, 3, 2, NOW(6), NULL, NULL),
(9, 3, 3, NOW(6), NULL, NULL);

-- Event: 강남 치밥
INSERT INTO events (id, title, description, latitude, longitude, code, name, address, start_time, end_time, community_id, associate_id, created_at, modified_at, deleted_at)
VALUES (4, '강남 치밥', '강남에서의 치킨 파티', 37.2973576, 127.1283168, 0, '감탄계 숯불치킨 강남역점', '서울특별시 강남구 강남대로',
        '2025-08-01 00:00:00', '2025-08-03 23:59:59', 1, 1, NOW(6), NULL, NULL);

INSERT INTO memories (id, event_id, created_at, modified_at, deleted_at)
VALUES (4, 4, NOW(6), NULL, NULL);

INSERT INTO memory_associate (id, memory_id, associate_id, created_at, modified_at, deleted_at)
VALUES 
(10, 4, 1, NOW(6), NULL, NULL),
(11, 4, 2, NOW(6), NULL, NULL),
(12, 4, 3, NOW(6), NULL, NULL);


-- Posts for Memory 1 (양평 엠티)
INSERT INTO posts (id, content, memory_id, associate_id, created_at, modified_at, deleted_at)
VALUES
(1, '양평 엠티 사진 공유합니다!', 1, 1, NOW(6), NULL, NULL),
(2, '정말 즐거운 MT였어요!', 1, 2, NOW(6), NULL, NULL),
(3, '다음에도 꼭 같이 가요~', 1, 3, NOW(6), NULL, NULL);

-- Post Images for Memory 1
INSERT INTO post_images (id, url, hash, post_id, created_at, modified_at, deleted_at)
VALUES
(1, '/test_images/image1.png', 'hash_img1', 1, NOW(6), NULL, NULL),
(2, '/test_images/image2.png', 'hash_img2', 1, NOW(6), NULL, NULL),
(3, '/test_images/image3.png', 'hash_img3', 2, NOW(6), NULL, NULL),
(4, '/test_images/image1.png', 'hash_img1_dup1', 3, NOW(6), NULL, NULL);

-- Posts for Memory 2 (강남 치킨)
INSERT INTO posts (id, content, memory_id, associate_id, created_at, modified_at, deleted_at)
VALUES
(4, '강남 치킨 모임 사진입니다!', 2, 1, NOW(6), NULL, NULL),
(5, '치킨 정말 맛있었어요!', 2, 2, NOW(6), NULL, NULL),
(6, '다음에 또 가요!', 2, 3, NOW(6), NULL, NULL);

-- Post Images for Memory 2
INSERT INTO post_images (id, url, hash, post_id, created_at, modified_at, deleted_at)
VALUES
(5, '/test_images/image1.png', 'hash_img1_m2', 4, NOW(6), NULL, NULL),
(6, '/test_images/image2.png', 'hash_img2_m2', 4, NOW(6), NULL, NULL),
(7, '/test_images/image3.png', 'hash_img3_m2', 5, NOW(6), NULL, NULL),
(8, '/test_images/image4.png', 'hash_img4_m2', 6, NOW(6), NULL, NULL);

-- Posts for Memory 3 (양봉장 인터뷰)
INSERT INTO posts (id, content, memory_id, associate_id, created_at, modified_at, deleted_at)
VALUES
(7, '양봉장 인터뷰 사진 공유합니다!', 3, 1, NOW(6), NULL, NULL),
(8, '좋은 경험이었어요.', 3, 2, NOW(6), NULL, NULL),
(9, '양봉장 멋졌습니다.', 3, 3, NOW(6), NULL, NULL);

-- Post Images for Memory 3
INSERT INTO post_images (id, url, hash, post_id, created_at, modified_at, deleted_at)
VALUES
(9, '/test_images/image2.png', 'hash_img2_m3', 7, NOW(6), NULL, NULL),
(10, '/test_images/image1.png', 'hash_img1_m3', 8, NOW(6), NULL, NULL);

-- Posts for Memory 4 (강남 치밥)
INSERT INTO posts (id, content, memory_id, associate_id, created_at, modified_at, deleted_at)
VALUES
(10, '강남 치밥 모임 사진입니다!', 4, 1, NOW(6), NULL, NULL),
(11, '치밥이 최고였어요.', 4, 2, NOW(6), NULL, NULL),
(12, '다음에도 치밥 콜!', 4, 3, NOW(6), NULL, NULL);

-- Post Images for Memory 4
INSERT INTO post_images (id, url, hash, post_id, created_at, modified_at, deleted_at)
VALUES
(11, '/test_images/image1.png', 'hash_img1_m4', 10, NOW(6), NULL, NULL),
(12, '/test_images/image2.png', 'hash_img2_m4', 10, NOW(6), NULL, NULL),
(13, '/test_images/image3.png', 'hash_img3_m4', 11, NOW(6), NULL, NULL),
(14, '/test_images/image4.png', 'hash_img4_m4', 12, NOW(6), NULL, NULL);

-- additional starts --

-- =========================================
-- Memento seed — richer data, community 1
-- Uses picsum.photos for all images
-- =========================================

CREATE DATABASE IF NOT EXISTS memento CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE memento;

-- Keep your originals as-is, then extend/adjust below.
-- If you want to reset images for existing associates/posts, run these updates:
UPDATE associates SET profile_image_url = 'https://picsum.photos/seed/alice/256/256' WHERE id = 1;
UPDATE associates SET profile_image_url = 'https://picsum.photos/seed/bob/256/256' WHERE id = 2;
UPDATE associates SET profile_image_url = 'https://picsum.photos/seed/charlie/256/256' WHERE id = 3;

-- Replace previous /test_images with picsum on existing post_images
UPDATE post_images SET url='https://picsum.photos/seed/m1p1a/1200/800' WHERE id=1;
UPDATE post_images SET url='https://picsum.photos/seed/m1p1b/1200/800' WHERE id=2;
UPDATE post_images SET url='https://picsum.photos/seed/m1p2a/1200/800' WHERE id=3;
UPDATE post_images SET url='https://picsum.photos/seed/m1p3a/1200/800' WHERE id=4;

UPDATE post_images SET url='https://picsum.photos/seed/m2p1a/1200/800' WHERE id=5;
UPDATE post_images SET url='https://picsum.photos/seed/m2p1b/1200/800' WHERE id=6;
UPDATE post_images SET url='https://picsum.photos/seed/m2p2a/1200/800' WHERE id=7;
UPDATE post_images SET url='https://picsum.photos/seed/m2p3a/1200/800' WHERE id=8;

UPDATE post_images SET url='https://picsum.photos/seed/m3p1a/1200/800' WHERE id=9;
UPDATE post_images SET url='https://picsum.photos/seed/m3p2a/1200/800' WHERE id=10;

UPDATE post_images SET url='https://picsum.photos/seed/m4p1a/1200/800' WHERE id=11;
UPDATE post_images SET url='https://picsum.photos/seed/m4p1b/1200/800' WHERE id=12;
UPDATE post_images SET url='https://picsum.photos/seed/m4p2a/1200/800' WHERE id=13;
UPDATE post_images SET url='https://picsum.photos/seed/m4p3a/1200/800' WHERE id=14;

-- -------------------------------------------------
-- Add more members (IDs continue from your current)
-- Existing last member id = 4
-- -------------------------------------------------
INSERT INTO members (id, name, email, birthday, kakao_id, created_at, modified_at, deleted_at) VALUES
(5,  'Dana',      'dana@example.com',      '1996-03-17', 10005, NOW(6), NULL, NULL),
(6,  'Evan',      'evan@example.com',      '1998-09-02', 10006, NOW(6), NULL, NULL),
(7,  'Fiona',     'fiona@example.com',     '1995-12-30', 10007, NOW(6), NULL, NULL),
(8,  'George',    'george@example.com',    '2000-01-14', 10008, NOW(6), NULL, NULL),
(9,  'Hana',      'hana@example.com',      '1997-05-22', 10009, NOW(6), NULL, NULL),
(10, 'Ian',       'ian@example.com',       '1999-07-11', 10010, NOW(6), NULL, NULL),
(11, 'Jisoo',     'jisoo@example.com',     '1998-10-05', 10011, NOW(6), NULL, NULL),
(12, 'Kenta',     'kenta@example.com',     '1996-02-25', 10012, NOW(6), NULL, NULL),
(13, 'Lena',      'lena@example.com',      '1997-08-19', 10013, NOW(6), NULL, NULL),
(14, 'Minho',     'minho@example.com',     '1995-04-09', 10014, NOW(6), NULL, NULL),
(15, 'Nora',      'nora@example.com',      '2000-11-28', 10015, NOW(6), NULL, NULL),
(16, 'Oscar',     'oscar@example.com',     '1998-01-07', 10016, NOW(6), NULL, NULL),
(17, 'Priya',     'priya@example.com',     '1999-03-03', 10017, NOW(6), NULL, NULL),
(18, 'Quinn',     'quinn@example.com',     '1997-06-16', 10018, NOW(6), NULL, NULL);

-- -------------------------------------------------
-- More associates for community 1 (IDs continue)
-- Existing associates 1..3
-- -------------------------------------------------
INSERT INTO associates (id, nickname, profile_image_url, introduction, achievement_id, member_id, community_id, created_at, modified_at, deleted_at) VALUES
(4,  'DanaD',   'https://picsum.photos/seed/dana/256/256',   'backend과 커피를 사랑합니다.', 6,  5,  1, NOW(6), NULL, NULL),
(5,  'EvanE',   'https://picsum.photos/seed/evan/256/256',   '데브옵스 빌드 장인.',           14, 6,  1, NOW(6), NULL, NULL),
(6,  'FiFi',    'https://picsum.photos/seed/fiona/256/256',  '취미로 사진 찍어요 📸',         9,  7,  1, NOW(6), NULL, NULL),
(7,  'G-Force', 'https://picsum.photos/seed/george/256/256', '프론트엔드 실험가.',            22, 8,  1, NOW(6), NULL, NULL),
(8,  'HanaH',   'https://picsum.photos/seed/hana/256/256',   '데이터로 수다 떠는 사람.',       13, 9,  1, NOW(6), NULL, NULL),
(9,  'IanDev',  'https://picsum.photos/seed/ian/256/256',    'Rust 덕후.',                    23, 10, 1, NOW(6), NULL, NULL),
(10, 'Jisu',    'https://picsum.photos/seed/jisoo/256/256',  'UI/UX 관심 많아요.',             2,  11, 1, NOW(6), NULL, NULL),
(11, 'Ken',     'https://picsum.photos/seed/kenta/256/256',  '테스트 자동화 애호가.',          6,  12, 1, NOW(6), NULL, NULL),
(12, 'LenaL',   'https://picsum.photos/seed/lena/256/256',   'PM & 도큐멘터리스트.',           1,  13, 1, NOW(6), NULL, NULL),
(13, 'Min',     'https://picsum.photos/seed/minho/256/256',  '알고리즘이 재밌다!',             17, 14, 1, NOW(6), NULL, NULL),
(14, 'Nor',     'https://picsum.photos/seed/nora/256/256',   '밈 수집가.',                     6,  15, 1, NOW(6), NULL, NULL),
(15, 'Oz',      'https://picsum.photos/seed/oscar/256/256',  '클라우드 네이티브 lover.',       18, 16, 1, NOW(6), NULL, NULL),
(16, 'Pri',     'https://picsum.photos/seed/priya/256/256',  'ML이랑 요가 좋아요.',            3,  17, 1, NOW(6), NULL, NULL),
(17, 'Q',       'https://picsum.photos/seed/quinn/256/256',  '게임잼 참가러.',                 11, 18, 1, NOW(6), NULL, NULL);

-- -------------------------------------------------
-- Diverse associate_stats (IDs continue from 3)
-- -------------------------------------------------
INSERT INTO associate_stats (id, associate_id, consecutive_attendance_days, last_attended_at, uploaded_reaction_count, used_reaction_count, guest_book_count, uploaded_profile_image_count, registered_profile_image_count, uploaded_post_image_count, created_memory_count, joined_memory_count, mbti_test_count, f_mbti_count, t_mbti_count) VALUES
(4,  4,  5,  '2025-09-05 08:30:00', 10, 22, 3, 1, 1, 8,  2, 6, 1, 1, 0),
(5,  5,  2,  '2025-09-03 21:12:00',  4,  9, 1, 2, 1, 3,  1, 4, 0, 0, 1),
(6,  6,  12, '2025-08-29 10:05:00', 18, 15, 2, 1, 1, 12, 3, 7, 2, 2, 0),
(7,  7,  0,  NULL,                   0,  0, 0, 0, 0, 0,  0, 0, 0, 0, 0),
(8,  8,  7,  '2025-09-07 13:44:00', 21, 19, 5, 1, 1, 9,  2, 8, 1, 1, 0),
(9,  9,  1,  '2025-09-09 09:02:00',  2,  5, 1, 1, 1, 2,  0, 2, 0, 0, 1),
(10, 10, 3,  '2025-09-01 18:26:00',  9,  6, 0, 1, 1, 4,  1, 1, 1, 1, 0),
(11, 11, 9,  '2025-09-08 20:10:00', 14, 12, 2, 2, 1, 6,  2, 3, 1, 0, 1),
(12, 12, 4,  '2025-09-04 07:55:00',  6, 10, 0, 1, 1, 5,  1, 4, 0, 0, 1),
(13, 13, 6,  '2025-09-06 12:15:00', 12, 11, 1, 1, 1, 7,  2, 5, 2, 2, 0),
(14, 14, 8,  '2025-09-02 23:40:00', 25, 20, 4, 2, 1, 10, 3, 6, 1, 1, 0),
(15, 15, 0,  NULL,                   0,  0, 0, 0, 0, 0,  0, 0, 0, 0, 0),
(16, 16, 11, '2025-09-10 06:32:00', 17, 18, 3, 1, 1, 11, 4, 9, 2, 1, 1),
(17, 17, 2,  '2025-09-05 15:07:00',  5,  7, 1, 1, 1, 3,  1, 2, 0, 0, 1);

-- -------------------------------------------------
-- New events (community_id = 1), with memories
-- Existing events 1..4, memories 1..4
-- -------------------------------------------------

-- 5) 한강 라이딩 (past)
INSERT INTO events (id, title, description, latitude, longitude, code, name, address, start_time, end_time, community_id, associate_id, created_at, modified_at, deleted_at) VALUES
(5, '한강 라이딩', '여의도에서 뚝섬까지 단체 라이딩', 37.528, 126.932, 0, '여의도 한강공원', '서울 영등포구 여의도동',
 '2024-10-12 09:00:00', '2024-10-12 13:00:00', 1, 7, NOW(6), NULL, NULL);
INSERT INTO memories (id, event_id, created_at, modified_at, deleted_at) VALUES (5, 5, NOW(6), NULL, NULL);

-- 6) 보드게임 나이트 (past)
INSERT INTO events VALUES
(6, '보드게임 나이트', '신촌 보드게임 카페에서 밤샘', 37.559, 126.942, 0, '신촌 보드게임카페', '서울 서대문구 신촌로',
 '2025-02-15 19:00:00', '2025-02-16 02:00:00', 1, 12, NOW(6), NULL, NULL);
INSERT INTO memories VALUES (6, 6, NOW(6), NULL, NULL);

-- 7) 부산 워케이션 (past)
INSERT INTO events VALUES
(7, '부산 워케이션', '해운대 근처 숙소에서 원격근무 & 회식', 35.163, 129.163, 0, '해운대', '부산 해운대구',
 '2025-04-18 10:00:00', '2025-04-20 18:00:00', 1, 15, NOW(6), NULL, NULL);
INSERT INTO memories VALUES (7, 7, NOW(6), NULL, NULL);

-- 8) 홍대 스터디데이 (past)
INSERT INTO events VALUES
(8, '홍대 스터디데이', '각자 사이드프로젝트 집중', 37.556, 126.923, 0, '홍대입구', '서울 마포구 와우산로',
 '2025-05-24 10:00:00', '2025-05-24 18:00:00', 1, 10, NOW(6), NULL, NULL);
INSERT INTO memories VALUES (8, 8, NOW(6), NULL, NULL);

-- 9) 남이섬 피크닉 (past)
INSERT INTO events VALUES
(9, '남이섬 피크닉', '강변 산책하고 사진 많이 찍기', 37.790, 127.525, 0, '남이섬', '강원 춘천시 남산면',
 '2025-06-29 09:30:00', '2025-06-29 17:00:00', 1, 6, NOW(6), NULL, NULL);
INSERT INTO memories VALUES (9, 9, NOW(6), NULL, NULL);

-- 10) 제주 해커톤 (future)
INSERT INTO events VALUES
(10, '제주 해커톤', '한 달 준비한 미니 해커톤', 33.499, 126.531, 0, '제주시청 인근', '제주 제주시',
 '2025-10-04 09:00:00', '2025-10-05 21:00:00', 1, 16, NOW(6), NULL, NULL);
INSERT INTO memories VALUES (10, 10, NOW(6), NULL, NULL);

-- 11) 송리단길 브런치 (future)
INSERT INTO events VALUES
(11, '송리단길 브런치', '브런치 먹고 석촌호수 산책', 37.509, 127.104, 0, '송리단길', '서울 송파구 송파동',
 '2025-09-21 11:00:00', '2025-09-21 15:00:00', 1, 8, NOW(6), NULL, NULL);
INSERT INTO memories VALUES (11, 11, NOW(6), NULL, NULL);

-- 12) 강릉 사진 출사 (future)
INSERT INTO events VALUES
(12, '강릉 사진 출사', '오전 바다, 오후 카페 투어', 37.751, 128.876, 0, '안목해변', '강원 강릉시',
 '2025-11-08 07:00:00', '2025-11-08 20:00:00', 1, 6, NOW(6), NULL, NULL);
INSERT INTO memories VALUES (12, 12, NOW(6), NULL, NULL);

-- -------------------------------------------------
-- Memory participants (continue ids from 12)
-- -------------------------------------------------
INSERT INTO memory_associate (id, memory_id, associate_id, created_at, modified_at, deleted_at) VALUES
(13, 5,  4, NOW(6), NULL, NULL),
(14, 5,  7, NOW(6), NULL, NULL),
(15, 5, 11, NOW(6), NULL, NULL),

(16, 6, 12, NOW(6), NULL, NULL),
(17, 6, 10, NOW(6), NULL, NULL),
(18, 6, 14, NOW(6), NULL, NULL),

(19, 7, 15, NOW(6), NULL, NULL),
(20, 7,  8, NOW(6), NULL, NULL),
(21, 7,  6, NOW(6), NULL, NULL),

(22, 8, 10, NOW(6), NULL, NULL),
(23, 8,  9, NOW(6), NULL, NULL),
(24, 8,  4, NOW(6), NULL, NULL),

(25, 9,  6, NOW(6), NULL, NULL),
(26, 9, 13, NOW(6), NULL, NULL),
(27, 9,  1, NOW(6), NULL, NULL),

(28, 10, 16, NOW(6), NULL, NULL),
(29, 10, 17, NOW(6), NULL, NULL),
(30, 10,  5, NOW(6), NULL, NULL),

(31, 11,  8, NOW(6), NULL, NULL),
(32, 11,  2, NOW(6), NULL, NULL),
(33, 11, 11, NOW(6), NULL, NULL),

(34, 12,  6, NOW(6), NULL, NULL),
(35, 12, 12, NOW(6), NULL, NULL),
(36, 12,  3, NOW(6), NULL, NULL);

-- -------------------------------------------------
-- Posts (continue ids from 12)
-- -------------------------------------------------
INSERT INTO posts (id, content, memory_id, associate_id, created_at, modified_at, deleted_at) VALUES
-- m5 한강 라이딩
(13, '바람 미쳤다… 라이딩 맛집 한강!', 5, 7, NOW(6), NULL, NULL),
(14, '뚝섬에서 단체 사진 📷', 5, 11, NOW(6), NULL, NULL),
(15, '초보도 완주 성공! 다음에 또 해요', 5, 4, NOW(6), NULL, NULL),

-- m6 보드게임 나이트
(16, '테라포밍 마스 대결의 밤… 🔥', 6, 12, NOW(6), NULL, NULL),
(17, '신촌 야식으로 충전 완료', 6, 14, NOW(6), NULL, NULL),

-- m7 부산 워케이션
(18, '오전 바다, 오후 코드. 삶의 균형이란 ✨', 7, 15, NOW(6), NULL, NULL),
(19, '해운대 노을 색감 미쳤다', 7, 6, NOW(6), NULL, NULL),

-- m8 홍대 스터디데이
(20, '오로지 커밋만 남긴 하루', 8, 10, NOW(6), NULL, NULL),
(21, 'PR 4개 날렸습니다 😎', 8, 9, NOW(6), NULL, NULL),

-- m9 남이섬 피크닉
(22, '감성 폭발 피크닉 세팅', 9, 13, NOW(6), NULL, NULL),
(23, '사진 백장 찍은 날', 9, 6, NOW(6), NULL, NULL),

-- m10 제주 해커톤 (미래 일정이지만 준비 포스트)
(24, '주제 브레인스토밍 중… 아이디어 쏟아짐', 10, 16, NOW(6), NULL, NULL),

-- m11 송리단길 브런치
(25, '브런치→석호수 루틴 추천', 11, 8, NOW(6), NULL, NULL),

-- m12 강릉 사진 출사
(26, 'RAW 파일만 20GB 🤯', 12, 6, NOW(6), NULL, NULL),
(27, '안목해변 카페 투어 지도 정리해둠', 12, 12, NOW(6), NULL, NULL);

-- -------------------------------------------------
-- Post images (continue ids from 14)
-- -------------------------------------------------
INSERT INTO post_images (id, url, hash, post_id, created_at, modified_at, deleted_at) VALUES
-- m5
(15, 'https://picsum.photos/seed/ride_group/1200/800', 'hash_m5a', 13, NOW(6), NULL, NULL),
(16, 'https://picsum.photos/seed/ride_bridge/1200/800', 'hash_m5b', 14, NOW(6), NULL, NULL),
(17, 'https://picsum.photos/seed/ride_finish/1200/800', 'hash_m5c', 15, NOW(6), NULL, NULL),

-- m6
(18, 'https://picsum.photos/seed/boardgame1/1200/800', 'hash_m6a', 16, NOW(6), NULL, NULL),
(19, 'https://picsum.photos/seed/boardgame2/1200/800', 'hash_m6b', 17, NOW(6), NULL, NULL),

-- m7
(20, 'https://picsum.photos/seed/busan_sunset/1200/800', 'hash_m7a', 19, NOW(6), NULL, NULL),
(21, 'https://picsum.photos/seed/busan_work/1200/800',   'hash_m7b', 18, NOW(6), NULL, NULL),

-- m8
(22, 'https://picsum.photos/seed/hongdae_focus/1200/800', 'hash_m8a', 20, NOW(6), NULL, NULL),
(23, 'https://picsum.photos/seed/hongdae_commits/1200/800','hash_m8b', 21, NOW(6), NULL, NULL),

-- m9
(24, 'https://picsum.photos/seed/nami_picnic/1200/800',   'hash_m9a', 22, NOW(6), NULL, NULL),
(25, 'https://picsum.photos/seed/nami_forest/1200/800',   'hash_m9b', 23, NOW(6), NULL, NULL),

-- m10
(26, 'https://picsum.photos/seed/jeju_hackathon/1200/800','hash_m10a', 24, NOW(6), NULL, NULL),

-- m11
(27, 'https://picsum.photos/seed/songridangil/1200/800',  'hash_m11a', 25, NOW(6), NULL, NULL),

-- m12
(28, 'https://picsum.photos/seed/gangneung_sea/1200/800', 'hash_m12a', 26, NOW(6), NULL, NULL),
(29, 'https://picsum.photos/seed/anmok_cafe/1200/800',    'hash_m12b', 27, NOW(6), NULL, NULL);

-- -------------------------------------------------
-- Nice-to-have: a few extra posts on existing memories for variety
-- -------------------------------------------------
INSERT INTO posts (id, content, memory_id, associate_id, created_at, modified_at, deleted_at) VALUES
(28, '양평 MT 회고: 장점 5, 개선점 3 정리', 1, 2, NOW(6), NULL, NULL),
(29, '강남 치킨 소스 랭킹 만들어봄', 2, 1, NOW(6), NULL, NULL),
(30, '양봉장 인터뷰 기록 정리했어요 (노션 링크)', 3, 3, NOW(6), NULL, NULL);

INSERT INTO post_images (id, url, hash, post_id, created_at, modified_at, deleted_at) VALUES
(30, 'https://picsum.photos/seed/retrospective/1200/800', 'hash_ext1', 28, NOW(6), NULL, NULL),
(31, 'https://picsum.photos/seed/chicken_sauce/1200/800', 'hash_ext2', 29, NOW(6), NULL, NULL),
(32, 'https://picsum.photos/seed/beefarm_notes/1200/800', 'hash_ext3', 30, NOW(6), NULL, NULL);

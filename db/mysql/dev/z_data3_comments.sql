USE memento;

-- ======================================================
-- 공통: 헬퍼 (시퀀스/범위/타겟 포스트)
-- ======================================================
-- 1) 1..4 생성용 시퀀스 (_seq_4)
DROP TEMPORARY TABLE IF EXISTS _seq_4;
CREATE TEMPORARY TABLE _seq_4 AS
SELECT (@r:=@r+1) AS n
FROM information_schema.COLUMNS, (SELECT @r:=0) x
LIMIT 4;

-- 2) 댓글용: 이모지/보이스 범위
SET @emoji_min_id := (SELECT MIN(id) FROM emoji);
SET @emoji_max_id := (SELECT MAX(id) FROM emoji);
SET @emoji_span   := @emoji_max_id - @emoji_min_id + 1;

SET @voice_min_id := (SELECT MIN(id) FROM voices);
SET @voice_max_id := (SELECT MAX(id) FROM voices);
SET @voice_span   := @voice_max_id - @voice_min_id + 1;

-- 3) 최근 300개 포스트를 타겟으로
DROP TEMPORARY TABLE IF EXISTS _target_posts;
CREATE TEMPORARY TABLE _target_posts AS
SELECT id AS post_id
FROM posts
ORDER BY id DESC
LIMIT 300;

-- ======================================================
-- A) COMMENTS (각 포스트당: EMOJI 2개 + VOICE 1개)
--  - URL은 등록된 emoji/voices 테이블에서만 선택
-- ======================================================

-- EMOJI #1
INSERT INTO comments (url, type, post_id, associate_id, created_at, modified_at, deleted_at)
SELECT
  e.url,
  'EMOJI',
  p.post_id,
  1 + (p.post_id % 17),
  NOW(6), NULL, NULL
FROM _target_posts p
JOIN emoji e
  ON e.id = @emoji_min_id + (p.post_id % @emoji_span);

-- EMOJI #2 (오프셋 변경)
INSERT INTO comments (url, type, post_id, associate_id, created_at, modified_at, deleted_at)
SELECT
  e.url,
  'EMOJI',
  p.post_id,
  1 + ((p.post_id + 5) % 17),
  NOW(6), NULL, NULL
FROM _target_posts p
JOIN emoji e
  ON e.id = @emoji_min_id + ((p.post_id + 7) % @emoji_span);

-- VOICE #1 (temporary/비temporary 섞여 있음)
INSERT INTO comments (url, type, post_id, associate_id, created_at, modified_at, deleted_at)
SELECT
  v.url,
  'VOICE',
  p.post_id,
  1 + ((p.post_id + 11) % 17),
  NOW(6), NULL, NULL
FROM _target_posts p
JOIN voices v
  ON v.id = @voice_min_id + ((p.post_id + 3) % @voice_span);

-- ======================================================
-- B) GUEST BOOKS (각 associate 당 3개: TEXT, EMOJI, VOICE)
--  - EMOJI/VOICE 타입의 content에는 등록 URL 저장
-- ======================================================

-- TEXT
INSERT INTO guest_books (type, content, name, associate_id, created_at, modified_at, deleted_at)
SELECT
  'TEXT',
  CONCAT('방문 감사! 자동 메시지 for associate #', a.id),
  NULL,
  a.id,
  NOW(6), NULL, NULL
FROM associates a;

-- EMOJI
INSERT INTO guest_books (type, content, name, associate_id, created_at, modified_at, deleted_at)
SELECT
  'EMOJI',
  e.url,
  NULL,
  a.id,
  NOW(6), NULL, NULL
FROM associates a
JOIN emoji e
  ON e.id = @emoji_min_id + ((a.id * 7) % @emoji_span);

-- VOICE
INSERT INTO guest_books (type, content, name, associate_id, created_at, modified_at, deleted_at)
SELECT
  'VOICE',
  v.url,
  NULL,
  a.id,
  NOW(6), NULL, NULL
FROM associates a
JOIN voices v
  ON v.id = @voice_min_id + ((a.id * 5) % @voice_span);

-- ======================================================
-- C) MBTI TESTS (각 associate 당 4개 생성)
-- ======================================================

DROP TEMPORARY TABLE IF EXISTS _mbti_types;
CREATE TEMPORARY TABLE _mbti_types (
  idx INT PRIMARY KEY,
  mbti CHAR(4) NOT NULL
);

INSERT INTO _mbti_types (idx, mbti) VALUES
(0,'ISFP'),(1,'ISFJ'),(2,'ISTP'),(3,'ISTJ'),
(4,'INFP'),(5,'INFJ'),(6,'INTP'),(7,'INTJ'),
(8,'ESFP'),(9,'ESFJ'),(10,'ESTP'),(11,'ESTJ'),
(12,'ENFP'),(13,'ENFJ'),(14,'ENTP'),(15,'ENTJ');

INSERT INTO mbti_test (mbti, from_associate_id, to_associate_id, created_at, modified_at, deleted_at)
SELECT
  t.mbti,
  1 + ((a.id + s.n) % 17),
  a.id,
  NOW(6), NULL, NULL
FROM associates a
JOIN _seq_4 s
JOIN _mbti_types t
  ON t.idx = (a.id + s.n) % 16;

-- ======================================================
-- D) ACHIEVEMENT_ASSOCIATE (중복 무시, 각 associate 최대 2개 추가)
--  - 기존 유니크키(achievement_id, associate_id) 충돌 시 무시
-- ======================================================

INSERT IGNORE INTO achievement_associate (achievement_id, associate_id, created_at, modified_at, deleted_at)
SELECT
  1 + ((a.id * 3) % 33),
  a.id,
  NOW(6), NULL, NULL
FROM associates a;

INSERT IGNORE INTO achievement_associate (achievement_id, associate_id, created_at, modified_at, deleted_at)
SELECT
  1 + ((a.id * 7) % 33),
  a.id,
  NOW(6), NULL, NULL
FROM associates a;

-- ------------------------------------------------------
-- 정리 (옵션)
-- ------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS _seq_4, _target_posts, _mbti_types;

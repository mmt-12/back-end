USE memento;

/* -------------------------------------------------------
   Build small sequence helpers (no CTEs)
------------------------------------------------------- */
DROP TEMPORARY TABLE IF EXISTS _seq_30, _seq_20, _seq_4, _seq_5;
CREATE TEMPORARY TABLE _seq_30 AS
SELECT ROW_NUMBER() OVER () AS n FROM information_schema.COLUMNS LIMIT 30;
CREATE TEMPORARY TABLE _seq_20 AS
SELECT ROW_NUMBER() OVER () AS n FROM information_schema.COLUMNS LIMIT 20;
CREATE TEMPORARY TABLE _seq_5 AS
SELECT ROW_NUMBER() OVER () AS n FROM information_schema.COLUMNS LIMIT 5;
CREATE TEMPORARY TABLE _seq_4 AS
SELECT ROW_NUMBER() OVER () AS n FROM information_schema.COLUMNS LIMIT 4;

/* -------------------------------------------------------
   +30 Emojis
------------------------------------------------------- */
INSERT INTO emoji (name, url, associate_id, created_at, modified_at, deleted_at)
SELECT
  CONCAT('custom_', LPAD(n,2,'0')),
  CONCAT('https://picsum.photos/seed/emoji_custom_', n, '/640/640'),
  1 + (n-1) % 17,
  NOW(6), NULL, NULL
FROM _seq_30;

/* -------------------------------------------------------
   +30 Voices (alternate temporary TRUE/FALSE)
------------------------------------------------------- */
INSERT INTO voices (name, url, temporary, associate_id, created_at, modified_at, deleted_at)
SELECT
  CONCAT('voice_', LPAD(n,2,'0')),
  CONCAT('https://github.com/user-attachments/files/90000', LPAD(n,3,'0'), '/seed-voice-', n, '.mp3'),
  IF(n % 2 = 0, FALSE, TRUE),
  1 + (n-1) % 17,
  NOW(6), NULL, NULL
FROM _seq_30;

/* -------------------------------------------------------
   Helpers for valid comment URL joins
------------------------------------------------------- */
SET @emoji_min_id := (SELECT MIN(id) FROM emoji);
SET @emoji_max_id := (SELECT MAX(id) FROM emoji);
SET @emoji_span   := @emoji_max_id - @emoji_min_id + 1;

SET @voice_min_id := (SELECT MIN(id) FROM voices);
SET @voice_max_id := (SELECT MAX(id) FROM voices);
SET @voice_span   := @voice_max_id - @voice_min_id + 1;

/* -------------------------------------------------------
   +20 Events (community stays 1) + one Memory each
------------------------------------------------------- */
INSERT INTO events
(title, description, latitude, longitude, code, name, address,
 start_time, end_time, community_id, associate_id, created_at, modified_at, deleted_at)
SELECT
  CONCAT('Auto Event #', n),
  CONCAT('자동 생성 이벤트 설명 #', n),
  37.50 + (n * 0.01), 127.00 + (n * 0.01),
  0,
  CONCAT('장소-', n),
  CONCAT('서울 어딘가-', n),
  DATE_ADD('2025-01-01 10:00:00', INTERVAL n DAY),
  DATE_ADD('2025-01-01 18:00:00', INTERVAL n DAY),
  1,
  1 + (n-1) % 17,
  NOW(6), NULL, NULL
FROM _seq_20;

DROP TEMPORARY TABLE IF EXISTS _new_events;
CREATE TEMPORARY TABLE _new_events AS
SELECT id AS event_id
FROM events
ORDER BY id DESC
LIMIT 20;

INSERT INTO memories (event_id, created_at, modified_at, deleted_at)
SELECT event_id, NOW(6), NULL, NULL
FROM _new_events
ORDER BY event_id;

/* Participants: 3 per new memory */
DROP TEMPORARY TABLE IF EXISTS _new_mems;
CREATE TEMPORARY TABLE _new_mems AS
SELECT id AS mem_id
FROM memories
WHERE event_id IN (SELECT event_id FROM _new_events);

INSERT INTO memory_associate (memory_id, associate_id, created_at, modified_at, deleted_at)
SELECT m.mem_id,
       1 + ((m.mem_id + s.n - 2) % 17),
       NOW(6), NULL, NULL
FROM _new_mems m
JOIN _seq_5 s;

/* -------------------------------------------------------
   Posts: 3 per memory (→ 60 posts)
------------------------------------------------------- */
INSERT INTO posts (content, memory_id, associate_id, created_at, modified_at, deleted_at)
SELECT
  CONCAT('자동 생성 포스트 m', m.mem_id, ' #', s.n),
  m.mem_id,
  1 + (m.mem_id + s.n - 2) % 17,
  NOW(6), NULL, NULL
FROM _new_mems m
JOIN _seq_5 s;

DROP TEMPORARY TABLE IF EXISTS _new_posts;
CREATE TEMPORARY TABLE _new_posts AS
SELECT p.id AS post_id
FROM posts p
JOIN _new_mems m ON m.mem_id = p.memory_id;

/* -------------------------------------------------------
   Post images: 3 per post (picsum)
------------------------------------------------------- */
INSERT INTO post_images (url, hash, post_id, created_at, modified_at, deleted_at)
SELECT
  CONCAT('https://picsum.photos/seed/p', p.post_id, '_', s.n, '/1200/800'),
  CONCAT('hash_p', p.post_id, '_', s.n),
  p.post_id,
  NOW(6), NULL, NULL
FROM _new_posts p
JOIN _seq_5 s;

/* -------------------------------------------------------
   Cleanup (optional)
------------------------------------------------------- */
DROP TEMPORARY TABLE IF EXISTS _seq_30, _seq_20, _seq_4, _seq_3, _new_events, _new_mems, _new_posts, _mbti_types;
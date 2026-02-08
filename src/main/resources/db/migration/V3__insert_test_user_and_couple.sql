-- 1. 유저 2명 생성
INSERT IGNORE INTO user (id, nickname, birthday, provider, provider_id, is_initialized, created_at, updated_at)
VALUES (1, '테스트유저A', '2000-01-02', 'KAKAO', '12345', true, NOW(), NOW()),
       (2, '테스트유저B', '2000-01-03', 'KAKAO', '67890', true, NOW(), NOW());

-- 2. 커플 연결
INSERT IGNORE INTO couple (id, user_a_id, user_b_id, status, last_question_index, last_question_date, created_at,
                           updated_at)
VALUES (1, 1, 2, 'ACTIVE', 0, '2024-01-01', NOW(), NOW());


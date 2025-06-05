-- 統合テスト用初期データ
INSERT INTO assignees (id, name, photo_url) VALUES
(1, 'テスト担当者1', '/images/test1.jpg'),
(2, 'テスト担当者2', '/images/test2.jpg');

INSERT INTO issues (id, summary, description, status, assignee_id) VALUES
(1, 'TODO課題', 'TODO状態のテスト課題', 'TODO', 1),
(2, 'DOING課題', 'DOING状態のテスト課題', 'DOING', 2),
(3, 'DONE課題', 'DONE状態のテスト課題', 'DONE', 1);

-- パスワードは 'password' をbcryptハッシュ化したもの
INSERT INTO users (id, email, password, enabled) VALUES
(1, 'user@example.com', '$2a$10$7GJOBzKJZmQo3lKGxb7l2uKnhZxzqhZY2G0AqJ1.2qo6tPjIhVdVi', true);

-- シーケンスの値をリセット（次のIDが確実に4以降になるように）
SELECT setval('assignees_id_seq', 3);
SELECT setval('issues_id_seq', 4);
SELECT setval('users_id_seq', 2); 
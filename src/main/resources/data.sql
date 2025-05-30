INSERT INTO issues (summary, description) VALUES ('バグA', 'バグがあります');
INSERT INTO issues (summary, description) VALUES ('機能要望B', 'Bに追加機能がほしいです');
INSERT INTO issues (summary, description) VALUES ('画面Cが遅い', '速くしてほしいです');

-- ユーザーデータ (パスワード: 'password' のBCryptハッシュ)
INSERT INTO users (username, password) VALUES ('user', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG');
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG');
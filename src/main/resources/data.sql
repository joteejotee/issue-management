-- 担当者データ
INSERT INTO assignees (name, photo_url) VALUES ('Isaac Castillejos', '/images/avatars/isaac-castillejos-VOQkzFIkF0Y-unsplash.png');
INSERT INTO assignees (name, photo_url) VALUES ('James Marty', '/images/avatars/james-marty-h1BuNJZzpC8-unsplash.png');
INSERT INTO assignees (name, photo_url) VALUES ('田中 太郎', '/images/avatars/nsplash-1.webp');

INSERT INTO issues (summary, description, status, assignee_id) VALUES ('ログイン機能でセッションタイムアウトエラー', 'ユーザーがログイン後30分経過すると「セッションが無効です」エラーが発生し、再ログインが必要になる問題を修正してください。', 'TODO', 1);
INSERT INTO issues (summary, description, status, assignee_id) VALUES ('レスポンシブデザインの改善', 'モバイル端末（iPhone SE, Galaxy S21）でヘッダーナビゲーションが崩れる問題があります。CSS Grid の設定を見直してレスポンシブ対応を改善してください。', 'TODO', 2);
INSERT INTO issues (summary, description, status, assignee_id) VALUES ('APIレスポンス速度の最適化', 'ユーザー一覧取得API（/api/users）のレスポンス時間が5秒以上かかっています。N+1問題の解決やクエリの最適化を実施してください。', 'TODO', 3);
INSERT INTO issues (summary, description, status, assignee_id) VALUES ('ダークモード対応', 'ユーザーからダークモード対応の要望が多数寄せられています。システム設定とユーザー設定の両方に対応したダークモード機能を実装してください。', 'TODO', 1);
INSERT INTO issues (summary, description, status, assignee_id) VALUES ('ファイルアップロード機能の容量制限', 'プロフィール画像アップロード時に10MB以上のファイルでエラーハンドリングが適切に動作しません。エラーメッセージの改善と進捗表示機能も追加してください。', 'TODO', 2);

-- ユーザーデータ (パスワード: 'password' のBCryptハッシュ)
INSERT INTO users (username, password) VALUES ('user', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG');
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG');
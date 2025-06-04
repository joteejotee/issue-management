-- 担当者データ
INSERT INTO assignees (name, photo_url) VALUES ('Isaac Castillejos', '/images/avatars/isaac-castillejos-VOQkzFIkF0Y-unsplash.png');
INSERT INTO assignees (name, photo_url) VALUES ('James Marty', '/images/avatars/james-marty-h1BuNJZzpC8-unsplash.png');
INSERT INTO assignees (name, photo_url) VALUES ('Emily Thompson', '/images/avatars/Emily-Thompson-nsplash.webp');
INSERT INTO assignees (name, photo_url) VALUES ('Daniel Miller', '/images/avatars/Daniel-Miller-unsplash.png');

-- TODO課題（未着手）- 5件
INSERT INTO issues (summary, description, status, assignee_id) VALUES ('ログイン機能でセッションタイムアウトエラー', 'ユーザーがログイン後30分経過すると「セッションが無効です」エラーが発生し、再ログインが必要になる問題を修正してください。', 'TODO', 1);
INSERT INTO issues (summary, description, status, assignee_id) VALUES ('レスポンシブデザインの改善', 'モバイル端末（iPhone SE, Galaxy S21）でヘッダーナビゲーションが崩れる問題があります。CSS Grid の設定を見直してレスポンシブ対応を改善してください。', 'TODO', 2);
INSERT INTO issues (summary, description, status, assignee_id) VALUES ('APIレスポンス速度の最適化', 'ユーザー一覧取得API（/api/users）のレスポンス時間が5秒以上かかっています。N+1問題の解決やクエリの最適化を実施してください。', 'TODO', 3);
INSERT INTO issues (summary, description, status, assignee_id) VALUES ('ダークモード対応', 'ユーザーからダークモード対応の要望が多数寄せられています。システム設定とユーザー設定の両方に対応したダークモード機能を実装してください。', 'TODO', 1);
INSERT INTO issues (summary, description, status, assignee_id) VALUES ('ファイルアップロード機能の容量制限', 'プロフィール画像アップロード時に10MB以上のファイルでエラーハンドリングが適切に動作しません。エラーメッセージの改善と進捗表示機能も追加してください。', 'TODO', 2);

-- DOING課題（作業中）- 3件
INSERT INTO issues (summary, description, status, assignee_id) VALUES ('検索機能の性能改善', 'キーワード検索で部分一致検索時に全文検索インデックスを使用するよう改修中。現在PostgreSQLのGINインデックス実装を検討しています。', 'DOING', 3);
INSERT INTO issues (summary, description, status, assignee_id) VALUES ('通知機能の実装', 'メール通知とWebプッシュ通知の両方に対応した通知システムを開発中。Spring Boot MailとWebSocket APIを使用した実装を進めています。', 'DOING', 4);
INSERT INTO issues (summary, description, status, assignee_id) VALUES ('CSVエクスポート機能追加', '課題一覧をCSV形式でエクスポートできる機能を実装中。Apache Commons CSVライブラリを使用してバッチ処理として実装しています。', 'DOING', 1);

-- DONE課題（完了）- 2件
INSERT INTO issues (summary, description, status, assignee_id) VALUES ('パスワード強度チェック機能', 'ユーザー登録時とパスワード変更時に強度チェック（8文字以上、英数字記号組み合わせ）を実装しました。フロントエンドでリアルタイム表示も対応済み。', 'DONE', 2);
INSERT INTO issues (summary, description, status, assignee_id) VALUES ('ログ出力レベルの調整', '本番環境でのログ出力レベルをINFOに変更し、デバッグ情報の出力を抑制しました。ログローテーションも日次で設定完了。', 'DONE', 4);

-- ユーザーデータ
INSERT INTO users (email, password) VALUES ('test@example.com', '$2a$10$h7qtYQoWgagMxYTXYo7Jj.IyX36F9vAtO8YML85a4zAfDIx..HKfO');
INSERT INTO users (email, password) VALUES ('admin@example.com', '$2a$10$h7qtYQoWgagMxYTXYo7Jj.IyX36F9vAtO8YML85a4zAfDIx..HKfO');
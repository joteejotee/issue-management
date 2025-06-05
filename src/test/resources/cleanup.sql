-- テスト前のデータベースクリーンアップ
DELETE FROM issues;
DELETE FROM assignees;
DELETE FROM users;
-- シーケンスをリセット
ALTER SEQUENCE assignees_id_seq RESTART WITH 1;
ALTER SEQUENCE issues_id_seq RESTART WITH 1;
ALTER SEQUENCE users_id_seq RESTART WITH 1; 
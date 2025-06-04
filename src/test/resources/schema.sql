-- テスト用データベーススキーマ

-- 担当者テーブル
CREATE TABLE IF NOT EXISTS assignees (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    photo_url VARCHAR(500) DEFAULT '/images/avatars/default.svg'
);

-- 課題テーブル
CREATE TABLE IF NOT EXISTS issues (
    id SERIAL PRIMARY KEY,
    summary VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'TODO',
    assignee_id BIGINT,
    FOREIGN KEY (assignee_id) REFERENCES assignees(id) ON DELETE SET NULL
);

-- ユーザーテーブル（認証用）
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- テストデータの挿入
INSERT INTO users (email, password, enabled) VALUES 
('test@example.com', '$2a$10$sPmOWktJ9XWWuiF8OPmqDeTPW6kXAIw8LM9MDG5rB0ZYGaJzl7WdC', true)
ON CONFLICT (email) DO NOTHING;

INSERT INTO assignees (name, photo_url) VALUES 
('テスト担当者1', '/images/avatars/test1.jpg'),
('テスト担当者2', '/images/avatars/test2.jpg')
ON CONFLICT DO NOTHING;

INSERT INTO issues (summary, description, status, assignee_id) VALUES 
('テスト課題1', 'テスト課題の説明1', 'TODO', 1),
('テスト課題2', 'テスト課題の説明2', 'DOING', 2),
('テスト課題3', 'テスト課題の説明3', 'DONE', 1)
ON CONFLICT DO NOTHING; 
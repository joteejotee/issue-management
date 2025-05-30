DROP TABLE IF EXISTS issues;
DROP TABLE IF EXISTS users;

create table issues (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    summary VARCHAR(256) NOT NULL,
    description VARCHAR(256) NOT NULL,
    status VARCHAR(10) DEFAULT 'TODO'  -- 課題のステータス：TODO, DOING, DONE
);

create table users (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

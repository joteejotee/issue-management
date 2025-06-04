DROP TABLE IF EXISTS issues;
DROP TABLE IF EXISTS assignees;
DROP TABLE IF EXISTS users;

create table assignees (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    photo_url VARCHAR(256) NOT NULL
);

create table issues (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    summary VARCHAR(256) NOT NULL,
    description VARCHAR(256) NOT NULL,
    status VARCHAR(10) DEFAULT 'TODO',  -- 課題のステータス：TODO, DOING, DONE
    assignee_id BIGINT REFERENCES assignees(id) ON DELETE SET NULL
);

create table users (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

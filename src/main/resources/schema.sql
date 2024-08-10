DROP TABLE IF EXISTS issues;

create table issues (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    summary VARCHAR(256) NOT NULL,
    description VARCHAR(256) NOT NULL
);

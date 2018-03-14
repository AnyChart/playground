# DEPRECATED. Now PostgreSQL is used.
CREATE TABLE repos (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    title VARCHAR(100),
    templates BOOLEAN,
    owner_id INTEGER REFERENCES users(id),
    create_date DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET=utf8;

CREATE TABLE versions (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name varchar(255) not NULL,
    repo_id integer references repos(id),
    commit varchar(40) not NULL,
    hidden BOOLEAN DEFAULT FALSE,
    config JSON,
    samples_count INTEGER,
    create_date DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET=utf8;

CREATE TABLE samples (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    -- repo
    version_id INTEGER references versions(id),
    -- save/update
    version INTEGER DEFAULT 0,
    -- hash CHAR(8),

    -- main info
    name VARCHAR(255),
    description TEXT,
    short_description VARCHAR(1024),
    url VARCHAR(255),

    -- meta
    tags JSON,
    deleted_tags JSON, -- to not auto-add already deleted tags generated from code when save/fork
    create_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    exports VARCHAR(255),
    views BIGINT DEFAULT 0,
    likes INTEGER DEFAULT 0,
    owner_id INTEGER REFERENCES users(id),
    preview BOOLEAN DEFAULT FALSE,
    latest BOOLEAN DEFAULT FALSE,

    -- html, js, css
    scripts TEXT,
    local_scripts TEXT,
    styles TEXT,

    code_type VARCHAR(16),
    code MEDIUMTEXT,

    style_type VARCHAR(16),
    style TEXT,

    markup_type VARCHAR(16),
    markup TEXT
) CHARACTER SET=utf8;

CREATE TABLE templates (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sample_id BIGINT REFERENCES samples(id),
    create_date DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET=utf8;

CREATE TABLE users (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  fullname VARCHAR(64),
  username VARCHAR(64),
  email VARCHAR(64),
  password VARCHAR(64),
  salt VARCHAR(64),
  permissions BIGINT,
  create_date DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET=utf8;

CREATE TABLE sessions (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  session CHAR (36) NOT NULL,
  user_id INT REFERENCES users(id),
  create_date DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET=utf8;

CREATE TABLE data_sources (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name CHAR (64) NOT NULL,
  title CHAR (64) NOT NULL,
  url VARCHAR(255) NOT NULL,
  type VARCHAR(64) NOT NULL,
  sets JSON,
  create_date DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET=utf8;

CREATE TABLE data_sets (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  data_source_id INTEGER REFERENCES data_sources(id),
  logo VARCHAR(64),
  name VARCHAR (64),
  title VARCHAR (64),
  description VARCHAR(256),
  tags JSON,
  source VARCHAR(256),
  sample MEDIUMTEXT,
  url VARCHAR(255) NOT NULL,
  data LONGTEXT,
  create_date DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET=utf8;

CREATE TABLE banned_tags (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  tag VARCHAR(64) NOT NULL UNIQUE,
  create_date DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET=utf8;

CREATE TABLE visits (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id INTEGER NOT NULL REFERENCES users(id),
  sample_id BIGINT NOT NULL REFERENCES samples(id),
  create_date DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET=utf8;

CREATE TABLE tags_mw (
  name VARCHAR(64),
  count BIGINT UNSIGNED
) CHARACTER SET=utf8;


CREATE INDEX sample_id_index ON visits (sample_id);

CREATE INDEX session_index ON sessions (session);
CREATE INDEX user_id_index ON sessions (user_id);

CREATE INDEX version_id_index ON samples (version_id);
CREATE INDEX latest_index ON samples (latest);
CREATE INDEX url_index ON samples (url);
CREATE INDEX owner_id ON samples (owner_id);
CREATE INDEX views_id ON samples (views);
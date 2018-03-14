DROP TABLE samples;
DROP TABLE versions;
DROP TABLE repos;
DROP TABLE users;

DROP SEQUENCE user_id_seq;
DROP SEQUENCE repo_id_seq;
DROP SEQUENCE version_id_seq;
DROP SEQUENCE sample_id_seq;


CREATE SEQUENCE user_id_seq;
CREATE TABLE users (
  id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('user_id_seq'),
  fullname VARCHAR(64),
  username VARCHAR(64),
  email VARCHAR(64),
  password VARCHAR(64),
  salt VARCHAR(64),
  permissions BIGINT,
  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE SEQUENCE repo_id_seq;
CREATE TABLE repos (
  id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('repo_id_seq'),
  name VARCHAR(100),
  title VARCHAR(100),
  templates BOOLEAN,
  owner_id BIGINT REFERENCES users(id),
  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE SEQUENCE version_id_seq;
CREATE TABLE versions (
  id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('version_id_seq'),
  name VARCHAR (255) NOT NULL,
  repo_id BIGINT REFERENCES repos(id),
  commit VARCHAR (40) NOT NULL,
  hidden BOOLEAN DEFAULT FALSE,
  config JSONB,
  samples_count INTEGER,
  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE SEQUENCE sample_id_seq;
CREATE TABLE samples (
  id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('sample_id_seq'),
  -- repo
  version_id BIGINT REFERENCES versions(id),
  -- save/update
  version INTEGER DEFAULT 0,

  -- main info
  name VARCHAR(255),
  description TEXT,
  short_description VARCHAR(1024),
  url VARCHAR(255),

  -- meta
  tags VARCHAR(128)[],
  deleted_tags VARCHAR(128)[], -- to not auto-add already deleted tags generated from code when save/fork
  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  views BIGINT DEFAULT 0,
  likes INTEGER DEFAULT 0,
  owner_id BIGINT REFERENCES users(id),
  preview BOOLEAN DEFAULT FALSE,
  latest BOOLEAN DEFAULT FALSE,

  -- html, js, css
  scripts VARCHAR(256)[],
  styles VARCHAR(256)[],

  code_type VARCHAR(16),
  code TEXT,

  style_type VARCHAR(16),
  style TEXT,

  markup_type VARCHAR(16),
  markup TEXT
);
CREATE INDEX tags_index ON samples USING gin (tags);
CREATE INDEX version_id_index ON samples (version_id);
CREATE INDEX latest_index ON samples (latest);
CREATE INDEX url_index ON samples (url);
CREATE INDEX owner_id ON samples (owner_id);
CREATE INDEX views_id ON samples (views);


CREATE SEQUENCE template_id_seq;
CREATE TABLE templates (
  id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('template_id_seq'),
  sample_id BIGINT REFERENCES samples(id),
  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE SEQUENCE session_id_seq;
CREATE TABLE sessions (
  id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('session_id_seq'),
  session CHAR (36) NOT NULL,
  user_id BIGINT REFERENCES users(id),
  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX session_index ON sessions (session);
CREATE INDEX user_id_index ON sessions (user_id);


CREATE SEQUENCE data_source_id_seq;
CREATE TABLE data_sources (
  id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('data_source_id_seq'),
  name VARCHAR (64) NOT NULL,
  title VARCHAR (64) NOT NULL,
  url VARCHAR(255) NOT NULL,
  type VARCHAR(64) NOT NULL,
  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE SEQUENCE data_set_id_seq;
CREATE TABLE data_sets (
  id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('data_set_id_seq'),
  data_source_id BIGINT REFERENCES data_sources(id),
  logo VARCHAR(64),
  name VARCHAR (64),
  title VARCHAR (64),
  description VARCHAR(256),
  tags VARCHAR(128),
  source VARCHAR(256),
  sample TEXT,
  url VARCHAR(255) NOT NULL,
  data TEXT,
  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE SEQUENCE banned_tag_id_seq;
CREATE TABLE banned_tags (
  id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('banned_tag_id_seq'),
  tag VARCHAR(128) NOT NULL UNIQUE,
  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE SEQUENCE visit_id_seq;
CREATE TABLE visits (
  id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('visit_id_seq'),
  user_id BIGINT NOT NULL REFERENCES users(id),
  sample_id BIGINT NOT NULL REFERENCES samples(id),
  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX sample_id_index ON visits (sample_id);


CREATE SEQUENCE canonical_visit_id_seq;
CREATE TABLE canonical_visits (
  id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('canonical_visit_id_seq'),
  user_id BIGINT NOT NULL REFERENCES users(id),
  url VARCHAR(255) NOT NULL,
  repo_id BIGINT REFERENCES repos(id),
  create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (user_id, url, repo_id)
);
CREATE INDEX url_canonical_visits_index ON canonical_visits (url);



CREATE MATERIALIZED VIEW tags AS
  SELECT tags.nm AS name,
         regexp_replace(regexp_replace(regexp_replace(lower(tags.nm), '[^a-z0-9]', '-', 'g'), '-[-]+', '-', 'g'), '(-$|^-)', '', 'g') AS id,
    count
  FROM
    (SELECT unnest(tags) AS nm,
            count(*) AS count
     FROM samples
     WHERE samples.latest
           AND samples.id NOT IN (SELECT sample_id FROM templates)
     GROUP BY nm
     ORDER BY COUNT DESC) tags
  WHERE nm NOT IN (SELECT tag FROM banned_tags)
WITH DATA;

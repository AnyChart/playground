-- name: sql-add-repo<!
INSERT INTO repos (`name`, `owner_id`) VALUES (:name, :owner_id);

-- name: sql-repos
SELECT * FROM repos;

-- name: sql-repo-by-name
SELECT * FROM repos WHERE `name` = :name;


-- name: sql-add-version<!
INSERT INTO versions (`name`, repo_id, commit, hidden, config, samples_count) VALUES(:name, :repo_id, :commit, :hidden, :config, :samples_count);

-- name: sql-versions
SELECT * FROM versions WHERE repo_id = :repo_id;

-- name: sql-version-by-name
SELECT * FROM versions WHERE repo_id = :repo_id AND `name` = :name;

-- name: sql-delete-version!
DELETE FROM versions WHERE id = :id;

-- name: sql-show-version!
UPDATE versions SET hidden = false WHERE repo_id = :repo_id AND id = :id;



-- name: sql-samples
SELECT * FROM samples;

-- name: sql-samples-by-ids
SELECT samples.*, versions.`name` as version_name, repos.name as repo_name FROM samples
   LEFT JOIN versions ON samples.version_id = versions.id
   LEFT JOIN repos ON versions.repo_id = repos.id
   WHERE samples.id IN (:ids);

-- name: sql-top-samples
SELECT samples.id, samples.name, samples.author, samples.views, samples.likes, samples.create_date, samples.url, samples.version, samples.version_id,
  samples.tags, samples.description, samples.short_description, samples.preview,
  versions.`name` as version_name, repos.name as repo_name,
  users.username, users.fullname
  FROM samples
  LEFT JOIN versions ON samples.version_id = versions.id
  LEFT JOIN repos ON versions.repo_id = repos.id
  JOIN users ON samples.owner_id = users.id
  JOIN (SELECT id FROM samples ORDER BY likes DESC, views DESC LIMIT :offset, :count) as optimize_samples
  ON optimize_samples.id = samples.id ORDER BY likes DESC, views DESC;

-- name: sql-samples-by-version
SELECT samples.id, samples.name, samples.author, samples.views, samples.likes, samples.create_date, samples.url, samples.version, samples.version_id,
  samples.tags, samples.description, samples.short_description, samples.preview,
  versions.`name` as version_name, repos.name as repo_name,
  users.username, users.fullname FROM samples
  JOIN versions ON samples.version_id = versions.id
  JOIN repos ON versions.repo_id = repos.id
  JOIN users ON samples.owner_id = users.id
  JOIN (SELECT id FROM samples WHERE version_id = :version_id ORDER BY likes DESC, views DESC LIMIT :offset, :count) as optimize_samples
  ON optimize_samples.id = samples.id ORDER BY likes DESC, views DESC;

-- name: sql-sample-version
SELECT version FROM samples WHERE url = :url ORDER BY version DESC;

-- name: sql-add-sample<!
INSERT INTO samples (`name`, `short_description`, `description`, `tags`, `styles`, `scripts`,
                      `markup`, `markup_type`, `style`, `style_type`, `code`, `code_type`,
                      `url`, `version`, `owner_id`) VALUES
                      (:name, :short_description, :description, :tags, :styles, :scripts,
                      :markup, :markup_type, :style, :style_type, :code, :code_type,
                      :url, :version, :owner_id);

-- name: sql-sample-by-url
SELECT * FROM samples WHERE version_id = :version_id AND url = :url;

-- name: sql-sample-template-by-url
SELECT samples.*, templates.id AS template_id FROM samples LEFT JOIN templates ON samples.id = templates.sample_id
  WHERE url = :url ORDER BY version DESC;

-- name: sql-sample-by-hash
SELECT * FROM samples WHERE version_id IS NULL AND url = :url AND version = :version;

-- name: sql-add-samples!
-- INSERT INTO samples (name, description, short_description, tags, export, scripts, local_scripts, styles, code_type, code, style_type, style, markup_type, markup) VALUES :values;

-- name: sql-delete-samples!
DELETE FROM samples WHERE version_id = :version_id;

-- name: sql-delete-samples-by-ids!
DELETE FROM samples WHERE id IN (:ids);

-- name: sql-update-sample-views!
UPDATE samples SET views = views + 1 WHERE id = :id;

-- name: sql-update-samples-preview!
UPDATE samples SET preview = :preview WHERE id IN (:ids);

-- name: sql-template-by-url
SELECT  samples.*, versions.`name` as version_name, repos.name as repo_name FROM samples
  JOIN templates ON samples.id = templates.sample_id
  JOIN versions ON samples.version_id = versions.id
  JOIN repos ON versions.repo_id = repos.id
  WHERE samples.url = :url;

-- name: sql-user-samples-without-preview
SELECT id, `name` FROM samples WHERE preview = false AND version_id IS NULL;

-- name: sql-repo-samples-without-preview
SELECT id, `name` FROM samples WHERE preview = false AND version_id IS NOT NULL;

-- name: sql-templates
SELECT * FROM samples JOIN templates ON samples.id = templates.sample_id;

-- name: sql-templates-sample-ids
SELECT sample_id FROM templates;

-- name: sql-delete-templates!
DELETE FROM templates;



------ users ------
-- name: sql-add-user<!
INSERT INTO users (`username`, `fullname`, `email`, `password`, `salt`, permissions)
  VALUES (:username, :fullname, :email, :password, :salt, :permissions);

-- name: sql-get-user-by-username-or-email
SELECT * FROM users WHERE username = :username or email = :username;

-- name: sql-get-user-by-username
SELECT * FROM users WHERE username = :username;

-- name: sql-get-user-by-email
SELECT * FROM users WHERE email = :email;

-- name: sql-delete-user!
DELETE FROM users WHERE id = :id;


------ sessions ------
--name: sql-add-session<!
INSERT INTO sessions (session, user_id) VALUES (:session, :user_id);

--name: sql-get-session
SELECT users.*,
       sessions.id as session_id, sessions.create_date, sessions.session
       FROM sessions JOIN users ON sessions.user_id = users.id WHERE session = :session;

--name: sql-delete-session!
DELETE FROM sessions WHERE session = :session;



------ tags ------------
--name: sql-tags
SELECT substring(tag, 2, LENGTH(tag)-2) name, count(*) count FROM (
SELECT JSON_EXTRACT(tags, CONCAT('$[', idx, ']')) AS tag FROM samples
JOIN (
SELECT  0  AS idx UNION
SELECT  1  UNION
SELECT  2  UNION
SELECT  3  UNION
SELECT  4  UNION
SELECT  5  UNION
SELECT  6  UNION
SELECT  7  UNION
SELECT  8  UNION
SELECT  9  UNION
SELECT  10) AS indexes
WHERE JSON_EXTRACT(tags, CONCAT('$[', idx, ']')) IS NOT NULL) as t1 GROUP BY tag ORDER BY count DESC;

--name: sql-top-tags
SELECT substring(tag, 2, LENGTH(tag)-2) name, count(*) count FROM (
SELECT JSON_EXTRACT(tags, CONCAT('$[', idx, ']')) AS tag FROM samples
JOIN (
SELECT  0  AS idx UNION
SELECT  1  UNION
SELECT  2  UNION
SELECT  3  UNION
SELECT  4  UNION
SELECT  5  UNION
SELECT  6  UNION
SELECT  7  UNION
SELECT  8  UNION
SELECT  9  UNION
SELECT  10) AS indexes
WHERE JSON_EXTRACT(tags, CONCAT('$[', idx, ']')) IS NOT NULL) as t1 GROUP BY tag ORDER BY count DESC LIMIT :limit;


-- name: sql-samples-by-tag
SELECT samples.id, samples.name, samples.author, samples.views, samples.likes, samples.create_date, samples.url, samples.version, samples.version_id,
  samples.tags, samples.description, samples.short_description, samples.preview,
  versions.`name` as version_name, repos.name as repo_name,
  users.username, users.fullname FROM samples
  LEFT JOIN versions ON samples.version_id = versions.id
  LEFT JOIN repos ON versions.repo_id = repos.id
  JOIN users ON samples.owner_id = users.id
  JOIN (SELECT id FROM samples WHERE JSON_CONTAINS(tags, CONCAT('["', :tag , '"]'))
        ORDER BY likes DESC, views DESC LIMIT :offset, :count) as optimize_samples
  ON optimize_samples.id = samples.id ORDER BY likes DESC, views DESC;


--SELECT * FROM samples WHERE JSON_CONTAINS(tags, CONCAT('["', :tag , '"]'));
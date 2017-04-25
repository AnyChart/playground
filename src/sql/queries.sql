-- name: sql-add-repo<!
INSERT INTO repos (`name`) VALUES (:name);

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
   LEFT JOIN versions ON samples.version_id = versions.id LEFT JOIN repos ON versions.repo_id = repos.id
   WHERE samples.id IN (:ids);

-- name: sql-samples-by-version
SELECT samples.id, samples.name, samples.author, samples.views, samples.likes, samples.create_date, samples.url, samples.version, samples.version_id,
  samples.tags, samples.description, samples.short_description, samples.preview,
  versions.`name` as version_name, repos.name as repo_name FROM samples
  JOIN versions ON samples.version_id = versions.id JOIN repos ON versions.repo_id = repos.id
  JOIN (SELECT id FROM samples WHERE version_id = :version_id ORDER BY likes DESC, views DESC LIMIT :offset, :count) as optimize_samples
  ON optimize_samples.id = samples.id ORDER BY likes DESC, views DESC;

-- name: sql-sample-version
SELECT version FROM samples WHERE url = :url ORDER BY version DESC;

-- name: sql-add-sample<!
INSERT INTO samples (`name`, `short_description`, `description`, `tags`, `styles`, `scripts`,
                      `markup`, `markup_type`, `style`, `style_type`, `code`, `code_type`,
                      `url`, `version`) VALUES
                      (:name, :short_description, :description, :tags, :styles, :scripts,
                      :markup, :markup_type, :style, :style_type, :code, :code_type,
                      :url, :version);

-- name: sql-top-samples
SELECT samples.id, samples.name, samples.author, samples.views, samples.likes, samples.create_date, samples.url, samples.version, samples.version_id,
  samples.tags, samples.description, samples.short_description, samples.preview,
  versions.`name` as version_name, repos.name as repo_name FROM samples
  LEFT JOIN versions ON samples.version_id = versions.id LEFT JOIN repos ON versions.repo_id = repos.id
  JOIN (SELECT id FROM samples ORDER BY likes DESC, views DESC LIMIT :offset, :count) as optimize_samples
  ON optimize_samples.id = samples.id ORDER BY likes DESC, views DESC;

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

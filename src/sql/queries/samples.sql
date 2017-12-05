-- name: sql-samples
SELECT * FROM samples;

-- name: sql-url-exist
SELECT id FROM samples WHERE url = :url;


-- name: sql-samples-by-ids
SELECT samples.*, versions.name as version_name, repos.name as repo_name FROM samples
   LEFT JOIN versions ON samples.version_id = versions.id
   LEFT JOIN repos ON versions.repo_id = repos.id
   WHERE samples.id IN (:ids);

-- name: sql-top-samples
SELECT samples.id, samples.name, samples.views, samples.likes, samples.create_date, samples.url, samples.version, samples.version_id,
  samples.tags, samples.description, samples.short_description, samples.preview, samples.latest,
  versions.name as version_name, repos.name as repo_name,
  users.username, users.fullname
  FROM samples
  LEFT JOIN versions ON samples.version_id = versions.id
  LEFT JOIN repos ON versions.repo_id = repos.id
  JOIN users ON samples.owner_id = users.id
  JOIN (SELECT samples.id FROM samples
        LEFT JOIN templates ON samples.id = templates.sample_id
        WHERE templates.sample_id IS NULL AND samples.latest
        ORDER BY likes DESC, views DESC, samples.name ASC LIMIT :count OFFSET :offset) as optimize_samples
  ON optimize_samples.id = samples.id ORDER BY likes DESC, views DESC, samples.name ASC;

-- name: sql-samples-by-version
SELECT samples.id, samples.name, samples.views, samples.likes, samples.create_date, samples.url, samples.version, samples.version_id,
  samples.tags, samples.description, samples.short_description, samples.preview,
  versions.name as version_name, repos.name as repo_name,
  users.username, users.fullname FROM samples
  JOIN versions ON samples.version_id = versions.id
  JOIN repos ON versions.repo_id = repos.id
  JOIN users ON samples.owner_id = users.id
  JOIN (SELECT id FROM samples WHERE version_id = :version_id
  ORDER BY likes DESC, views DESC, samples.name ASC LIMIT :count OFFSET :offset) as optimize_samples
  ON optimize_samples.id = samples.id ORDER BY likes DESC, views DESC, samples.name ASC;

-- name: sql-sample-version
SELECT version FROM samples WHERE url = :url ORDER BY version DESC;

-- name: sql-add-sample<!
INSERT INTO samples (name, short_description, description, tags, deleted_tags, styles, scripts,
                      markup, markup_type, style, style_type, code, code_type,
                      url, version, owner_id) VALUES
                      (:name, :short_description, :description, :tags, :deleted_tags, :styles, :scripts,
                      :markup, :markup_type, :style, :style_type, :code, :code_type,
                      :url, :version, :owner_id);

-- name: sql-sample-by-url
SELECT samples.*,
       users.fullname AS owner_fullname
FROM samples
JOIN users ON samples.owner_id = users.id
WHERE version_id = :version_id
  AND url = :url;

-- name: sql-sample-template-by-url
SELECT samples.*, templates.id AS template_id FROM samples LEFT JOIN templates ON samples.id = templates.sample_id
  WHERE url = :url ORDER BY version DESC;

-- name: sql-sample-by-hash
SELECT samples.*,
  users.fullname AS owner_fullname
FROM samples
  JOIN users ON samples.owner_id = users.id
WHERE version_id IS NULL AND url = :url AND version = :version;

-- name: sql-last-sample-by-hash
SELECT samples.*,
  users.fullname AS owner_fullname
FROM samples
  JOIN users ON samples.owner_id = users.id
WHERE version_id IS NULL AND url = :url
ORDER BY version DESC;


-- name: sql-add-samples!
-- INSERT INTO samples (name, description, short_description, tags, export, scripts, local_scripts, styles, code_type, code, style_type, style, markup_type, markup) VALUES :values;

-- name: sql-delete-samples!
DELETE FROM samples WHERE version_id = :version_id;

-- name: sql-update-sample-views!
UPDATE samples SET views = views + 1 WHERE id = :id;

-- name: sql-update-samples-preview!
UPDATE samples SET preview = :preview WHERE id IN (:ids);

-- name: sql-update-all-samples-latest!
UPDATE samples SET latest = :latest WHERE version_id IN
  (SELECT id FROM versions WHERE repo_id IN (SELECT id FROM repos WHERE name = :repo_name)
                          AND name <> :version_name);
-- name: sql-update-version-samples-latest!
UPDATE samples SET latest = :latest WHERE version_id IN
  (SELECT id FROM versions WHERE repo_id IN (SELECT id FROM repos WHERE name = :repo_name)
                           AND name = :version_name);
-- name: sql-update-all-user-samples-latest!
UPDATE samples SET latest = :latest WHERE url = :url AND version <> :version;
-- name: sql-update-version-user-samples-latest!
UPDATE samples SET latest = :latest WHERE url = :url AND version = :version;


-- name: sql-template-by-url
SELECT  samples.*, versions.name AS version_name, repos.name AS repo_name FROM samples
  JOIN templates ON samples.id = templates.sample_id
  JOIN versions ON samples.version_id = versions.id
  JOIN repos ON versions.repo_id = repos.id
  WHERE samples.url = :url;

-- name: sql-user-samples-without-preview
SELECT id, name FROM samples WHERE preview = false AND version_id IS NULL;

-- name: sql-repo-samples-without-preview
SELECT id, name FROM samples WHERE preview = false AND version_id IS NOT NULL;

-- name: sql-templates
SELECT * FROM samples JOIN templates ON samples.id = templates.sample_id ORDER BY samples.name;

-- name: sql-templates-sample-ids
SELECT sample_id FROM templates;

-- name: sql-delete-templates!
DELETE FROM templates;



-- name: sql-samples-by-tag
SELECT samples.id, samples.name, samples.views, samples.likes, samples.create_date, samples.url, samples.version, samples.version_id,
  samples.tags, samples.description, samples.short_description, samples.preview, samples.latest,
  versions.name as version_name, repos.name as repo_name,
  users.username, users.fullname FROM samples
  LEFT JOIN versions ON samples.version_id = versions.id
  LEFT JOIN repos ON versions.repo_id = repos.id
  JOIN users ON samples.owner_id = users.id
  JOIN (SELECT samples.id FROM samples
        LEFT JOIN templates ON samples.id = templates.sample_id
        WHERE templates.sample_id IS NULL
          AND tags @> ARRAY[:tag]
          AND samples.latest
        ORDER BY likes DESC, views DESC, samples.name ASC LIMIT :count OFFSET :offset) as optimize_samples
  ON optimize_samples.id = samples.id ORDER BY likes DESC, views DESC, samples.name ASC;


-- name: sql-group-samples
SELECT samples.url
FROM samples
WHERE version_id = :version_id
      AND url LIKE CONCAT(:url , '%') ORDER BY url ASC;


-- delete all repo ---
-- name: sql-delete-samples-by-repo-name!
DELETE FROM samples WHERE version_id IN (SELECT id FROM versions WHERE repo_id in (SELECT id FROM repos WHERE name = :name));

-- name: sql-delete-versions-by-repo-name!
DELETE FROM versions WHERE repo_id IN (SELECT id FROM repos WHERE name = :name);

-- name: sql-delete-repo-by-name!
DELETE FROM repos WHERE name = :name;

-- name: sql-sitemap-sample-urls
SELECT concat(repos.name, '/', samples.url) AS url,
       samples.create_date
FROM samples
  JOIN versions ON samples.version_id = versions.id
  JOIN repos ON versions.repo_id = repos.id
WHERE version_id AND latest AND samples.id NOT IN (SELECT sample_id FROM templates)
UNION
SELECT url, create_date
FROM samples
WHERE version_id IS NULL AND latest;


-- name: sql-get-visit
SELECT * FROM visits WHERE user_id = :user_id AND sample_id = :sample_id;

-- name: sql-visit!
INSERT INTO visits (user_id, sample_id ) VALUES (:user_id, :sample_id);

-- name: sql-delete-version-visits!
DELETE
FROM visits
WHERE sample_id IN
      (SELECT id
       FROM samples
       WHERE version_id = :version_id);

-- name: sql-delete-repo-visits!
DELETE
FROM visits
WHERE sample_id IN
      (SELECT id
       FROM samples
       WHERE version_id IN
             (SELECT id
              FROM versions
              WHERE repo_id = :repo_id));


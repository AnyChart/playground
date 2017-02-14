-- name: sql-add-project<!
INSERT INTO projects (`name`) VALUES (:name);

-- name: sql-projects
SELECT * FROM projects;

-- name: sql-project-by-name
SELECT * FROM projects WHERE `name` = :name;



-- name: sql-add-version<!
INSERT INTO versions (`key`, project_id, commit, hidden) VALUES(:key, :project_id, :commit, :hidden);

-- name: sql-versions
SELECT * FROM versions WHERE project_id = :project_id;

-- name: sql-version-by-name
SELECT * FROM versions WHERE project_id = :project_id AND `key` = :key;

-- name: sql-delete-version!
DELETE FROM versions WHERE id = :id;

-- name: sql-show-version!
UPDATE versions SET hidden = false WHERE project_id = :project_id AND id = :id;



-- name: sql-add-group<!
INSERT INTO groups (version_id, `index`, name, url, root, hidden, description, cover) VALUES (:version_id, :index, :name, :url, :root, :hidden, :description, :cover);

-- name: sql-delete-groups!
DELETE FROM groups WHERE version_id = :version_id;



-- name: sql-samples
SELECT * FROM samples;

-- name: sql-top-samples
SELECT samples.*, versions.id as version_id, versions.`key` as version_key, projects.name as project_name FROM samples
  JOIN versions ON samples.version_id = versions.id JOIN projects ON versions.project_id = projects.id LIMIT :count;

-- name: sql-sample-by-url
SELECT * FROM samples WHERE version_id = :version_id AND url = :url;

-- name: sql-add-samples!
INSERT INTO samples (name, description, short_description, tags, `index`, is_new, export, scripts, local_scripts, styles, code_type, code, style_type, style, markup_type, markup) VALUES :values;

-- name: sql-delete-samples!
DELETE FROM samples WHERE version_id = :version_id;




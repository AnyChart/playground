-- name: sql-add-version<!
INSERT INTO versions (name, repo_id, commit, hidden, config, samples_count)
VALUES(:name, :repo_id, :commit, :hidden, :config, :samples_count);

-- name: sql-versions
SELECT * FROM versions WHERE repo_id = :repo_id;

-- name: sql-versions-by-repo-name
SELECT name FROM versions WHERE repo_id = (SELECT id FROM repos WHERE name = :name);

-- name: sql-versions-by-repos-names
SELECT DISTINCT name FROM versions WHERE repo_id IN (SELECT id FROM repos WHERE name in (:repos_names));

-- name: sql-versions-repos
SELECT versions.name,
       versions.samples_count,
       repos.name AS repo_name
  FROM versions
  JOIN repos ON versions.repo_id = repos.id WHERE NOT repos.templates;

-- name: sql-version-by-name
SELECT * FROM versions WHERE repo_id = :repo_id AND name = :name;

-- name: sql-delete-version!
DELETE FROM versions WHERE id = :id;

-- name: sql-show-version!
UPDATE versions SET hidden = false WHERE repo_id = :repo_id AND id = :id;
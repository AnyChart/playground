-- name: sql-add-data-source<!
INSERT INTO data_sources (name, title, type, sets, url) VALUES (:name, :title, :type, :sets, :url);

-- name: sql-add-data-set<!
INSERT INTO data_sets (logo, name, title, description, tags, source, sample, data_source_id, url, data)
VALUES (:logo, :name, :title, :description, :tags, :source, :sample, :data_source_id, :url, :data);

-- name: sql-delete-data-sources!
DELETE FROM data_sources;

-- name: sql-delete-data-sets!
DELETE FROM data_sets;

-- name: sql-data-sets
SELECT
  data_sets.*,
  data_sources.name  AS data_source_name,
  data_sources.title AS data_source_title,
  data_sources.type  AS data_source_type,
  data_sources.id    AS data_source_id,
  data_sources.type
FROM data_sets
  JOIN data_sources ON data_sets.data_source_id = data_sources.id;

-- name: sql-data-sources
SELECT data_sources.*
FROM data_sources;

-- name: sql-top-data-sets
SELECT
  data_sets.*,
  data_sources.name  AS data_source_name,
  data_sources.title AS data_source_title,
  data_sources.type  AS data_source_type,
  data_sources.id    AS data_source_id,
  data_sources.type
FROM data_sets
  JOIN data_sources ON data_sets.data_source_id = data_sources.id
LIMIT :limit;

-- name: sql-data-set-by-name
SELECT
  data_sets.*,
  data_sources.name  AS data_source_name,
  data_sources.title AS data_source_title,
  data_sources.type  AS data_source_type,
  data_sources.id    AS data_source_id,
  data_sources.type
FROM data_sets
  JOIN data_sources ON data_sets.data_source_id = data_sources.id
WHERE
  data_sources.name = :data_source_name AND data_sets.name = :name;
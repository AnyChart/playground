-- name: sql-tags
SELECT name, count FROM tags;

-- name: sql-top-tags
SELECT name, count FROM tags LIMIT :limit;

-- name: sql-tag-name-by-id
SELECT name FROM tags WHERE id = :tag;

-- name: sql-update-tags!
REFRESH MATERIALIZED VIEW tags;

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
        AND tags && ARRAY(SELECT name FROM tags
                          WHERE id = regexp_replace(regexp_replace(regexp_replace(lower(:tag), '[^a-z0-9]', '-', 'g'), '-[-]+', '-', 'g'), '(-$|^-)', '', 'g'))::VARCHAR(128)[]
        AND samples.latest
        ORDER BY likes DESC, views DESC, samples.name ASC LIMIT :count OFFSET :offset) as optimize_samples
    ON optimize_samples.id = samples.id ORDER BY likes DESC, views DESC, samples.name ASC;
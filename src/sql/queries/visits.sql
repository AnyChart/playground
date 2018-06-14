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


-- name: sql-get-canonical-visit
SELECT * FROM canonical_visits
WHERE user_id = :user_id
      AND  url = :url
      AND (repo_id = :repo_id::BIGINT OR (:repo_id::BIGINT IS NULL AND repo_id IS NULL));

-- name: sql-canonical-visit!
INSERT INTO canonical_visits (user_id, repo_id, url) VALUES (:user_id, :repo_id, :url);

-- name: sql-update-sample-views-from-canonical-visits!
UPDATE samples SET views = (SELECT COUNT(*) FROM canonical_visits
                            WHERE url = :url AND (repo_id = :repo_id::BIGINT OR
                                                 (repo_id IS NULL AND :repo_id::BIGINT IS NULL)))
WHERE url = :url AND
      ((samples.version_id IS NULL AND :repo_id::BIGINT IS NULL) OR
       (version_id IN (SELECT id FROM versions WHERE versions.repo_id = :repo_id::BIGINT)));
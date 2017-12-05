-- name: sql-add-repo<!
INSERT INTO repos (name, title, templates, owner_id) VALUES (:name, :title, :templates, :owner_id);

-- name: sql-repos
SELECT * FROM repos ORDER BY title;

-- name: sql-repo-by-name
SELECT * FROM repos WHERE name = :name;
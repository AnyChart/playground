-- name: sql-tags
SELECT * FROM tags_mw;

-- name: sql-top-tags
SELECT * FROM tags_mw LIMIT :limit;

-- name: sql-clear-tags-mw!
DELETE FROM tags_mw;

-- name: sql-update-tags-mw!
INSERT INTO tags_mw
  SELECT substring(tag, 2, LENGTH(tag)-2) name,
         count(*) COUNT
  FROM
    ( SELECT JSON_EXTRACT(tags, CONCAT('$[', idx, ']')) AS tag
      FROM samples
        JOIN
        ( SELECT 0 AS idx
          UNION SELECT 1
          UNION SELECT 2
          UNION SELECT 3
          UNION SELECT 4
          UNION SELECT 5
          UNION SELECT 6
          UNION SELECT 7
          UNION SELECT 8
          UNION SELECT 9
          UNION SELECT 10) AS INDEXES
      WHERE samples.latest
            AND samples.id NOT IN
                (SELECT sample_id
                 FROM templates)
            AND JSON_EXTRACT(tags, CONCAT('$[', idx, ']')) IS NOT NULL) AS t1
  GROUP BY tag
  HAVING name NOT IN
         (SELECT tag
          FROM banned_tags)
  ORDER BY COUNT DESC;


-- name: sql-top-tags-samples
# SELECT DISTINCT tg.tag_count,
#   samples.id,
#   samples.name,
#   samples.views,
#   samples.likes,
#   samples.create_date,
#   samples.url,
#   samples.version,
#   samples.version_id,
#   samples.tags,
#   samples.description,
#   samples.short_description,
#   samples.preview,
#   samples.latest,
#   versions.`name` AS version_name,
#   repos.name AS repo_name,
#   users.username,
#   users.fullname
# FROM
#   (SELECT tag,
#      count(*) AS tag_count
#    FROM
#      (SELECT JSON_EXTRACT(tags, CONCAT('$[', idx, ']')) AS tag
#       FROM samples
#         JOIN
#         (SELECT 0 AS idx
#          UNION SELECT 1
#          UNION SELECT 2
#          UNION SELECT 3
#          UNION SELECT 4
#          UNION SELECT 5
#          UNION SELECT 6
#          UNION SELECT 7
#          UNION SELECT 8
#          UNION SELECT 9
#          UNION SELECT 10) AS indexes
#       WHERE samples.latest
#             AND JSON_EXTRACT(tags, CONCAT('$[', idx, ']')) IS NOT NULL) AS t1
#    GROUP BY tag
#    ORDER BY count(*) DESC) tg
#   JOIN samples ON samples.id =
#                   (SELECT id
#                    FROM samples
#                    WHERE tags LIKE CONCAT('%', tg.tag, '%')
#                    ORDER BY views DESC, likes DESC, name ASC
#                    LIMIT 1)
#   LEFT JOIN versions ON samples.version_id = versions.id
#   LEFT JOIN repos ON versions.repo_id = repos.id
#   LEFT JOIN users ON samples.owner_id = users.id
# ORDER BY tag_count DESC;
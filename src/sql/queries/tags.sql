-- name: sql-tags
SELECT tags.nm as name, count FROM
  (SELECT unnest(tags) as nm, count(*) as count
   FROM samples WHERE samples.latest
                      AND samples.id NOT IN (SELECT sample_id FROM templates)
   GROUP BY nm
   ORDER BY count DESC) tags
WHERE nm NOT IN (SELECT tag FROM banned_tags);

-- name: sql-top-tags
SELECT tags.nm as name, count FROM
  (SELECT unnest(tags) as nm, count(*) as count
   FROM samples WHERE samples.latest
                      AND samples.id NOT IN (SELECT sample_id FROM templates)
   GROUP BY nm
   ORDER BY count DESC) tags
WHERE nm NOT IN (SELECT tag FROM banned_tags)
LIMIT :limit;

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



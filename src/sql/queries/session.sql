-- name: sql-add-user<!
INSERT INTO users (username, fullname, email, password, salt, permissions)
VALUES (:username, :fullname, :email, :password, :salt, :permissions);

-- name: sql-get-user-by-username-or-email
SELECT *
FROM users
WHERE username = :username OR email = :username;

-- name: sql-get-user-by-username
SELECT *
FROM users
WHERE username = :username;

-- name: sql-get-user-by-email
SELECT *
FROM users
WHERE email = :email;

-- name: sql-delete-user!
DELETE FROM users
WHERE id = :id;

-- sessions
-- name: sql-add-session<!
INSERT INTO sessions (session, user_id) VALUES (:session, :user_id);

-- name: sql-get-session
SELECT
  users.*,
  sessions.id AS session_id,
  sessions.create_date,
  sessions.session
FROM sessions
  JOIN users ON sessions.user_id = users.id
WHERE session = :session;

-- name: sql-delete-session!
DELETE FROM sessions
WHERE session = :session;
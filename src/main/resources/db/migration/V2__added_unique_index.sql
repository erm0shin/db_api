-- noinspection SqlNoDataSourceInspectionForFile
-- noinspection SqlDialectInspectionForFile

DROP INDEX IF EXISTS idx_users_email;
DROP INDEX IF EXISTS idx_users_nickname;
DROP INDEX IF EXISTS idx_threads_slug;
DROP INDEX IF EXISTS idx_votes_user_thread;


CREATE UNIQUE INDEX idx_users_email
  ON users (LOWER(email));

CREATE UNIQUE INDEX idx_users_nickname
  ON users (LOWER(nickname));

CREATE UNIQUE INDEX idx_threads_slug
  ON threads (LOWER(slug));

CREATE UNIQUE INDEX idx_votes_user_thread
  ON votes (user_id, thread_id);
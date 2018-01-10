-- noinspection SqlNoDataSourceInspectionForFile
-- noinspection SqlDialectInspectionForFile

DROP INDEX IF EXISTS idx_forum_members;
-- DROP INDEX IF EXISTS idx_posts_thread;
DROP INDEX IF EXISTS idx_posts_thread_parent;
DROP INDEX IF EXISTS idx_forums_user;


CREATE INDEX idx_forum_members
  ON forum_members(forum_id, user_id);

-- CREATE INDEX idx_posts_thread
--   ON posts(thread_id);

CREATE INDEX idx_posts_thread_parent
  ON posts(thread_id, parent, path);

CREATE INDEX idx_forums_user
  ON forums(LOWER("user"));
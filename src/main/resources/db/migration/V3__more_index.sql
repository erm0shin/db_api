-- noinspection SqlNoDataSourceInspectionForFile
-- noinspection SqlDialectInspectionForFile

DROP INDEX IF EXISTS idx_forums_slug;
DROP INDEX IF EXISTS idx_threads_forum_created;
DROP INDEX IF EXISTS index_posts_path;
DROP INDEX IF EXISTS idx_posts_thread_id;
DROP INDEX IF EXISTS idx_posts_thread_path;
DROP INDEX IF EXISTS index_posts_parent;


CREATE INDEX idx_forums_slug
  ON forums(LOWER(slug));

CREATE INDEX idx_threads_forum_created
  ON threads(LOWER(forum), created);

CREATE INDEX index_posts_path
  ON posts USING GIN (path);

CREATE INDEX idx_posts_thread_id
  ON posts(thread_id, id);

CREATE INDEX idx_posts_thread_path
  ON posts(thread_id, path, id);

CREATE INDEX index_posts_parent
  ON posts(parent);
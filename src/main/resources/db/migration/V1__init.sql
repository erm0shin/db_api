-- noinspection SqlNoDataSourceInspectionForFile
-- noinspection SqlDialectInspectionForFile

SET SYNCHRONOUS_COMMIT = 'off';


DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS forums CASCADE;
DROP TABLE IF EXISTS threads CASCADE;
DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS votes CASCADE;
DROP TABLE IF EXISTS forum_members CASCADE;


CREATE TABLE IF NOT EXISTS users (
  id        BIGSERIAL  PRIMARY KEY,
  email     TEXT       NOT NULL,
  fullname  TEXT       NOT NULL,
  nickname  TEXT,
  about     TEXT
);


CREATE TABLE IF NOT EXISTS forums (
  id        BIGSERIAL   PRIMARY KEY,
  posts     BIGINT,
  slug      TEXT        NOT NULL,
  threads   INT,
  title     TEXT        NOT NULL,
  "user"    TEXT        NOT NULL
);


CREATE TABLE IF NOT EXISTS threads (
  id        SERIAL     PRIMARY KEY,
  author    TEXT          NOT NULL,
  created   TIMESTAMPTZ,
  forum     TEXT,
  message   TEXT          NOT NULL,
  slug      TEXT          UNIQUE,
  title     TEXT          NOT NULL,
  votes     INT
);


CREATE TABLE IF NOT EXISTS posts (
  id          BIGSERIAL     PRIMARY KEY,
  author      TEXT          NOT NULL,
  created     TIMESTAMPTZ   NOT NULL,
  forum       TEXT,
  isEdited    BOOLEAN       DEFAULT FALSE,
  message     TEXT          NOT NULL,
  parent      BIGINT,
--   path        BIGINT []     NOT NULL,
  thread_id   INT           NOT NULL --REFERENCES threads (id)
);


CREATE TABLE IF NOT EXISTS votes (
  user_id     BIGINT    NOT NULL, --REFERENCES users (id),
  thread_id   INT       NOT NULL, --REFERENCES threads (id),
  voice       INT       NOT NULL
);


-- CREATE TABLE IF NOT EXISTS forum_members (
--   user_id     BIGINT    REFERENCES users (id),
--   forum_id    BIGINT    REFERENCES forums (id)
-- );
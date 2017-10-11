-- noinspection SqlNoDataSourceInspectionForFile
-- noinspection SqlDialectInspectionForFile

SET SYNCHRONOUS_COMMIT = 'off';


DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS forums CASCADE;
DROP TABLE IF EXISTS threads CASCADE;
DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS votes CASCADE;
DROP TABLE IF EXISTS forum_members CASCADE;


CREATE TABLE users (
  id        BIGSERIAL  PRIMARY KEY,
  email     TEXT       NOT NULL,
  fullname  TEXT       NOT NULL,
  nickname  TEXT,
  about     TEXT
);


CREATE TABLE forums (
  id        BIGSERIAL   PRIMARY KEY,
  posts     BIGINT,
  slug      TEXT        NOT NULL,
  threads   INT,
  title     TEXT        NOT NULL,
  "user"    TEXT        NOT NULL
);


CREATE TABLE threads (
  id        SERIAL     PRIMARY KEY,
  author    TEXT          NOT NULL,
  created   TIMESTAMPTZ,
  forum     TEXT,
  message   TEXT          NOT NULL,
  slug      TEXT          UNIQUE,
  title     TEXT          NOT NULL,
  votes     INT
);


CREATE TABLE posts (
  id          BIGSERIAL     PRIMARY KEY,
  author      TEXT          NOT NULL,
  created     TIMESTAMPTZ   NOT NULL,
  forum       TEXT,
  isEdited    BOOLEAN       DEFAULT FALSE,
  message     TEXT          NOT NULL,
  parent      BIGINT,
  thread_id   INT           NOT NULL
);


CREATE TABLE votes (
  user_id     BIGINT    NOT NULL,
  thread_id   INT       NOT NULL,
  voice       INT       NOT NULL
);


CREATE TABLE forum_members (
  user_id     BIGINT,
  forum_id    BIGINT
);
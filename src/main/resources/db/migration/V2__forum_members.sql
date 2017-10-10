-- noinspection SqlNoDataSourceInspectionForFile
-- noinspection SqlDialectInspectionForFile

SET SYNCHRONOUS_COMMIT = 'off';

CREATE TABLE IF NOT EXISTS forum_members (
  user_id     BIGINT    REFERENCES users (id),
  forum_id    BIGINT    REFERENCES forums (id)
);
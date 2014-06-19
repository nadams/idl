# --- !Ups

ALTER TABLE Team
ADD COLUMN DateCreated DATETIME NOT NULL

# --- !Downs

ALTER TABLE Team
DROP COLUMN DateCreated

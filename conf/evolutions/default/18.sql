# --- !Ups
ALTER TABLE PlayerProfile ADD COLUMN IsApproved TINYINT(1) NOT NULL DEFAULT 0

# --- !Downs
ALTER TABLE PlayerProfile DROP COLUMN IsApproved

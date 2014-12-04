# --- !Ups
ALTER TABLE TeamPlayer
ADD COLUMN IsApproved TINYINT(1) NOT NULL DEFAULT 0
AFTER IsCaptain;

UPDATE TeamPlayer SET IsApproved = 1;

# --- !Downs
ALTER TABLE TeamPlayer
DROP COLUMN IsApproved;


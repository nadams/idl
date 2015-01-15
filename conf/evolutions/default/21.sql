# --- !Ups

ALTER TABLE Player 
ADD COLUMN DateCreated DATETIME NOT NULL
AFTER IsActive;

UPDATE Player SET DateCreated = NOW();

# --- !Downs
ALTER TABLE Player
DROP COLUMN DateCreated;

# --- !Ups

ALTER TABLE Player CHANGE Name PlayerName VARCHAR(32) NOT NULL;

ALTER TABLE Team CHANGE Name TeamName VARCHAR(32) NOT NULL;

# --- !Downs

ALTER TABLE Player CHANGE PlayerName Name VARCHAR(32) NOT NULL;

ALTER TABLE Team CHANGE TeamName Name VARCHAR(32) NOT NULL;

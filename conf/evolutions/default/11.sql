# --- !Ups

ALTER TABLE GameDemo ADD CONSTRAINT UQ_GameDemo_Game_Player UNIQUE (GameId, PlayerId)

# --- !Downs

ALTER TABLE GameDemo DROP KEY UQ_GameDemo_Game_Player

# --- !Ups

CREATE TABLE IF NOT EXISTS GameType (
	GameTypeId INT NOT NULL,
	GameTypeName VARCHAR(32) NOT NULL,

	CONSTRAINT PK_GameType PRIMARY KEY (GameTypeId),
	CONSTRAINT UQ_GameType_Name UNIQUE (GameTypeName)
);

INSERT INTO GameType (GameTypeId, GameTypename)
SELECT * FROM ( 
	SELECT 1, 'Regular'
) AS t
WHERE NOT EXISTS (
	SELECT GameTypeId 
	FROM GameType AS gt 
	WHERE gt.GameTypename = 'Regular'
) LIMIT 1; 

INSERT INTO GameType (GameTypeId, GameTypename)
SELECT * FROM ( 
	SELECT 2, 'Playoff'
) AS t
WHERE NOT EXISTS (
	SELECT GameTypeId 
	FROM GameType AS gt 
	WHERE gt.GameTypename = 'Playoff'
) LIMIT 1; 

ALTER TABLE Game ADD COLUMN GameTypeId INT NOT NULL DEFAULT 1 AFTER SeasonId;
ALTER TAbLE Game ADD CONSTRAINT FK_Game_GameType FOREIGN KEY (GameTypeId) REFERENCES GameType(GameTypeId);

# --- !Downs

ALTER TABLE Game DROP FOREIGN KEY FK_Game_GameType;
ALTER TABLE Game DROP COLUMN GameTypeId;
DROP TABLE GameType;

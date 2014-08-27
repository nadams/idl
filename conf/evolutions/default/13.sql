# --- !Ups

CREATE TABLE IF NOT EXISTS Round (
	RoundId INT NOT NULL AUTO_INCREMENT,
	GameId INT NOT NULL,
	MapNumber CHAR(5) NOT NULL,

	CONSTRAINT PK_Round PRIMARY KEY (RoundId),
	CONSTRAINT FK_Round_Game FOREIGN KEY Round(GameId) REFERENCES Game(GameId)
);

CREATE TABLE IF NOT EXISTS RoundResult (
	RoundResultId INT NOT NULL AUTO_INCREMENT,
	RoundId INT NOT NULL,
	PlayerId INT NOT NULL,
	Captures INT NOT NULL,
	PCaptures INT NOT NULL,
	Drops INT NOT NULL,
	Frags INT NOT NULL,
	Deaths INT NOT NULL,

	CONSTRAINT PK_RoundResult PRIMARY KEY (RoundResultId),
	CONSTRAINT FK_RoundResult_Round FOREIGN KEY RoundResult(RoundId) REFERENCES Round(RoundId),
	CONSTRAINT FK_RoundResult_Player FOREIGN KEY RoundResult(PlayerId) REFERENCES Player(PlayerId)
);

# --- !Downs

DROP TABLE Round;
DROP TABLE RoundResult;

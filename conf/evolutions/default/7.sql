# --- !Ups

CREATE TABLE IF NOT EXISTS PlayerProfile (
	PlayerId INT NOT NULL,
	ProfileId INT NOT NULL,

	CONSTRAINT PK_PlayerProfile PRIMARY KEY (PlayerId, ProfileId)
) ENGINE = INNODB;

# --- !Downs

DROP TABLE PlayerProfile;

# --- !Ups

CREATE TABLE IF NOT EXISTS Season (
	SeasonId INT NOT NULL AUTO_INCREMENT,
	Name VARCHAR(64) NOT NULL,
	StartDate DATETIME NOT NULL DEFAULT NOW(),
	EndDate DATETIME NOT NULL DEFAULT NOW(),

	CONSTRAINT PK_Season PRIMARY KEY (SeasonId),
	CONSTRAINT UQ_Season_Name UNIQUE (Name)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS Team (
	TeamId INT NOT NULL AUTO_INCREMENT,
	Name VARCHAR(32) NOT NULL,
	IsActive BIT NOT NULL DEFAULT 1,

	CONSTRAINT PK_Team PRIMARY KEY (TeamId),
	CONSTRAINT UQ_Team_Name UNIQUE (Name)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS Player (
	PlayerId INT NOT NULL AUTO_INCREMENT,
	Name VARCHAR(32) NOT NULL,
	IsActive BIT NOT NULL DEFAULT 1,

	CONSTRAINT PK_Player PRIMARY KEY (PlayerId),
	CONSTRAINT UQ_Player_Name UNIQUE (Name)
) ENGINE = INNODB;

# --- !Ups

CREATE TABLE IF NOT EXISTS Profile (
	ProfileId INT NOT NULL AUTO_INCREMENT,
	Email VARCHAR(64) NOT NULL,
	DisplayName VARCHAR(64) NOT NULL,
	Password CHAR(177) NOT NULL,
	PasswordExpired BIT NOT NULL DEFAULT 0,
	DateCreated DATETIME NOT NULL,
	LastLoginDate DATETIME NOT NULL,

	CONSTRAINT PK_Profile PRIMARY KEY (ProfileId),
	CONSTRAINT UQ_Profile_Email UNIQUE (Email)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS News (
	NewsId INT NOT NULL AUTO_INCREMENT,
	Subject VARCHAR(255) NOT NULL,
	DateCreated DATETIME NOT NULL,
	DateModified DATETIME NOT NULL,
	PostedByProfileId INT NOT NULL,
	Content TEXT NOT NULL,

	CONSTRAINT PK_News PRIMARY KEY (NewsId),
	CONSTRAINT FK_News_Profile FOREIGN KEY (PostedByProfileId) REFERENCES Profile(ProfileId)
) ENGINE = INNODB;

# Password is password
INSERT INTO Profile (Email, DisplayName, Password, DateCreated, LastLoginDate)
SELECT * FROM ( 
	SELECT
		'admin@intldoomleague.com', 
		'Admin',
		'a6419e0f6a0ec5368f9dbb032a25c4a5aa519183ff86cf1f:fc3e60aed01b43d7af1388b1924a61cdb1074f1636f92137bd540df82a16b5dfad22d0a7d0fd5ae524c520448b34f7540dda83c7e43adec935327960798240a9',
		now() as DateCreated,
		now() as LastLoginDate
) AS t
WHERE NOT EXISTS (
	SELECT Email 
	FROM Profile AS p 
	WHERE p.Email = 'admin@intldoomleague.com'
) LIMIT 1; 


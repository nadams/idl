# --- !Ups

CREATE TABLE IF NOT EXISTS Role (
	RoleId INT NOT NULL,
	RoleName VARCHAR(32) NOT NULL,

	CONSTRAINT PK_Role PRIMARY KEY (RoleId),
	CONSTRAINT UQ_Role_RoleName UNIQUE (RoleName)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS ProfileRole (
	RoleId INT NOT NULL,
	ProfileId INT NOT NULL,

	CONSTRAINT PK_ProfileRole PRIMARY KEY (RoleId, ProfileId),
	CONSTRAINT FK_ProfileRole_Role FOREIGN KEY (RoleId) REFERENCES Role(RoleId),
	CONSTRAINT FK_ProfileRole_Profile FOREIGN KEY (ProfileId) REFERENCES Profile(ProfileId)
);

INSERT INTO Role (RoleId, RoleName)
SELECT * FROM ( 
	SELECT 1, 'SuperAdmin'
) AS t
WHERE NOT EXISTS (
	SELECT RoleId 
	FROM Role AS r 
	WHERE r.RoleName = 'SuperAdmin'
) LIMIT 1; 

SET @AdminProfileId = (
	SELECT ProfileId 
	FROM Profile AS p
	WHERE p.Email = 'admin@intldoomleague.com'
);

INSERT INTO ProfileRole (RoleId, ProfileId)
SELECT * FROM ( 
	SELECT 1, @AdminProfileId
) AS t
WHERE NOT EXISTS (
	SELECT *
	FROM ProfileRole AS pr 
	WHERE pr.RoleId = 1 AND pr.ProfileId = @AdminProfileId
) LIMIT 1; 
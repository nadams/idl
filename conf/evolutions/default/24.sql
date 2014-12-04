# --- !Ups
CREATE TABLE PasswordToken(
  PasswordTokenId INT NOT NULL AUTO_INCREMENT,
  ProfileId INT NOT NULL,
  Token CHAR(48) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  DateCreated DATETIME NOT NULL,
  IsClaimed TINYINT(1) NOT NULL DEFAULT 0,

  CONSTRAINT PK_PasswordToken PRIMARY KEY (PasswordTokenId),
  CONSTRAINT UQ_PasswordToken_Token UNIQUE (Token),
  CONSTRAINT FK_PasswordToken_Profile FOREIGN KEY (ProfileId) REFERENCES Profile(ProfileId)
) ENGINE = INNODB;

# --- !Downs
DROP TABLE PasswordToken;


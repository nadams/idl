# --- !Ups
ALTER TABLE PlayerProfile 
ADD CONSTRAINT FK_PlayerProfile_Player 
FOREIGN KEY (PlayerId) REFERENCES Player(PlayerId);

ALTER TABLE PlayerProfile 
ADD CONSTRAINT FK_PlayerProfile_Profile 
FOREIGN KEY (ProfileId) REFERENCES Profile(ProfileId);

# --- !Downs
ALTER TABLE PlayerProfile
DROP CONSTRAINT FK_PlayerProfile_Player;

ALTER TABLE PlayerProfile
DROP CONSTRAINT FK_PlayerProfile_Profile;

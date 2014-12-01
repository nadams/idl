# --- !Ups
ALTER TABLE Profile 
ADD CONSTRAINT UQ_Profile_DisplayName UNIQUE (DisplayName);

# --- !Downs
ALTER TABLE Profile 
DROP INDEX UQ_Profile_DisplayName; 


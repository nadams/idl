# --- !Ups
INSERT INTO Role (RoleId, RoleName)
SELECT * FROM (SELECT 3, 'User') AS t
WHERE NOT EXISTS (
	SELECT 1 
	FROM Role AS r
	WHERE r.RoleId = 3
) LIMIT 1;

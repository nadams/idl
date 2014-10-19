# --- !Ups

CREATE TABLE IF NOT EXISTS idlsmf_messages (
  id_msg INT UNSIGNED PRIMARY KEY NOT NULL,
  id_board INT NOT NULL,
  subject VARCHAR(255) NOT NULL,
  poster_name VARCHAR(255) NOT NULL,
  body TEXT NOT NULL,
  poster_time INT UNSIGNED NOT NULL
);

CREATE TABLE IF NOT EXISTS idlsmf_topics (
  id_topic INT PRIMARY KEY NOT NULL,
  id_first_msg INT NOT NULL
);

CREATE TABLE IF NOT EXISTS idlsmf_boards (
  id_board INT PRIMARY KEY NOT NULL,
  name VARCHAR(255) NOT NULL
);

INSERT INTO idlsmf_boards(id_board, name)
SELECT * FROM ( 
	SELECT 1, 'IDL News'
) AS t
WHERE NOT EXISTS (
	SELECT name 
	FROM idlsmf_boards AS b 
	WHERE b.name = 'IDL News'
) LIMIT 1; 

INSERT INTO idlsmf_topics(id_topic, id_first_msg)
SELECT * FROM (
  SELECT 1 AS id_topic, 1 AS id_first_msg
) AS t 
WHERE NOT EXISTS (
  SELECT *
  FROM idlsmf_topics AS topics
  WHERE topics.id_topic = 1 AND topics.id_first_msg = 1
) LIMIT 1;

INSERT INTO idlsmf_messages(id_msg, id_board, subject, poster_name, body, poster_time)
SELECT * FROM (
  SELECT 1 AS id_msg, 1 AS id_board, 'Test Forum News Content', 'admin', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque at suscipit massa. Mauris nec nunc at nisl efficitur faucibus. Proin et nulla vitae magna ornare faucibus. Suspendisse vehicula dolor velit, et dignissim augue molestie quis. Phasellus vel massa non orci dignissim vulputate. Nulla facilisi. Donec euismod arcu sit amet massa convallis ultrices. Vestibulum molestie congue elit et fermentum. Duis auctor mi in placerat interdum. Sed ut mi vitae velit egestas tempor. Maecenas vitae metus varius, accumsan lacus id, scelerisque urna. Nullam lacinia in massa a semper. Quisque eu viverra enim. Maecenas sit amet consectetur leo. Integer porttitor lacinia ligula, nec iaculis ante feugiat et. Quisque rhoncus leo eget eleifend pulvinar. Nam vitae hendrerit purus.', 1339969095
) AS t
WHERE NOT EXISTS (
  SELECT *
  FROM idlsmf_messages AS m WHERE m.id_msg = 1
) LIMIT 1;

# --- !Downs

DROP TABLE idlsmf_boards;
DROP TABLE idlsmf_topics;
DROP TABLE idlsmf_messages;


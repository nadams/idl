# --- !Ups

CREATE TABLE IF NOT EXISTS idlsmf_messages (
  id_msg INT PRIMARY KEY NOT NULL,
  id_board INT NOT NULL,
  subject VARCHAR(255) NOT NULL,
  poster_name VARCHAR(255) NOT NULL,
  body TEXT NOT NULL,
  poster_time INT NOT NULL
);

CREATE TABLE IF NOT EXISTS idlsmf_topics (
  id_topic INT PRIMARY KEY NOT NULL,
  id_first_msg INT NOT NULL
);

CREATE TABLE IF NOT EXISTS idlsmf_boards (
  id_board INT PRIMARY KEY NOT NULL,
  name VARCHAR(255) NOT NULL
);

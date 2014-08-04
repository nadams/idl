# --- !Ups
CREATE TEMPORARY TABLE WeekValues (
	Id INT NOT NULL,
	Value VARCHAR(16) NOT NULL
);

INSERT INTO WeekValues (Id, Value) VALUES 
(1, 'Week 1'),
(2, 'Week 2'),
(3, 'Week 3'),
(4, 'Week 4'),
(5, 'Week 5'),
(6, 'Week 6'),
(7, 'Week 7'),
(8, 'Week 8'),
(9, 'Week 9'),
(10, 'Week 10');

INSERT INTO Week (WeekId, Name)
SELECT * FROM WeekValues
ON DUPLICATE KEY UPDATE WeekId = WeekId;

# --- !Downs
TRUNCATE TABLE Week;

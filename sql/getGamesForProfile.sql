SELECT 
	g.GameId,
	g.WeekId,
	g.SeasonId,
	g.ScheduledPlayTime,
	g.DateCompleted
FROM Profile AS p
	INNER JOIN PlayerProfile AS pp ON p.ProfileId = pp.ProfileId
	INNER JOIN Player AS p2 ON pp.PlayerId = p2.PlayerId
	INNER JOIN TeamPlayer AS tp ON p2.PlayerId = tp.PlayerId
	INNER JOIN Team AS t ON tp.TeamId = t.TeamId
	INNER JOIN ( 
		SELECT 
			GameId AS GameId, 
			Team1Id AS TeamId
		FROM TeamGame

		UNION ALL
		
		SELECT 
			GameId AS GameId, 
			Team2Id AS TeamId 
		FROM TeamGame
	) AS tg ON t.TeamId = tg.TeamId
	INNER JOIN Game AS g on tg.GameId = g.GameId
WHERE p.Email = {email}

package data

case class GameResult(gameResultId: Int, gameId: Int, playerId: Int, captures: Int, pCaptures: Int, drops: Int, frags: Int, deaths: Int) {
  val fragPercentage : Double = if(frags > 0) deaths.toDouble / frags * 100.0 else 100.0
  val capturePercentage : Double = 0.0
  val pickupPercentage : Double = 0.0
  val stopPercentage : Double = 0.0
}

object GameResult {
  def apply(x: (Int, Int, Int, Int, Int, Int, Int, Int)) : GameResult = 
    GameResult(x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8)
}

case class TeamGameResultRecord(teamId: Int, teamName: String, gameId: Int, weekId: Int, captures: Int)

object TeamGameResultRecord {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  lazy val selectResultsBySeasonId = 
    s"""
      SELECT
        t.${TeamSchema.teamId},
        t.${TeamSchema.name},
        g.${GameSchema.gameId},
        g.${GameSchema.weekId},
        CAST(SUM(gr.${GameResultSchema.captures}) AS SIGNED INTEGER) AS Captures,
        CAST(SUM(gr.${GameResultSchema.frags}) AS SIGNED INTEGER) AS Frags
      FROM ${GameSchema.tableName} AS g
        INNER JOIN ${GameResultSchema.tableName} AS gr ON g.${GameSchema.gameId} = gr.${GameResultSchema.gameId}
        INNER JOIN (
          SELECT DISTINCT
            tp.${TeamPlayerSchema.teamId},
            tp.${TeamPlayerSchema.playerId},
            tg.${TeamGameSchema.gameId}
          FROM ${SeasonSchema.tableName} AS s
            INNER JOIN ${TeamSeasonSchema.tableName} AS ts ON s.${SeasonSchema.seasonId} = ts.${TeamSeasonSchema.seasonId}
            INNER JOIN ${TeamSchema.tableName} AS t1 ON ts.${TeamSeasonSchema.teamId} = t1.${TeamSchema.teamId}
            INNER JOIN ${TeamSchema.tableName} AS t2 ON ts.${TeamSeasonSchema.teamId} = t2.${TeamSchema.teamId}
            INNER JOIN ${TeamGameSchema.tableName} AS tg ON t1.${TeamSchema.teamId} = tg.${TeamGameSchema.team1Id}
              OR t2.${TeamSchema.teamId} = tg.${TeamGameSchema.team2Id}
            INNER JOIN ${TeamPlayerSchema.tableName} AS tp ON t1.${TeamSchema.teamId} = tp.${TeamPlayerSchema.teamId}
              OR t2.${TeamSchema.teamId} = tp.${TeamPlayerSchema.teamId}
          WHERE ({seasonId} IS NULL AND NOW() BETWEEN s.${SeasonSchema.startDate} AND s.${SeasonSchema.endDate})
            OR s.${SeasonSchema.seasonId} = {seasonId}
        ) AS stats ON gr.${GameResultSchema.gameId} = stats.${TeamGameSchema.gameId}
          AND gr.${GameResultSchema.playerId} = stats.${TeamPlayerSchema.playerId}
        INNER JOIN ${TeamSchema.tableName} AS t ON stats.${TeamPlayerSchema.teamId} = t.${TeamSchema.teamId}
      GROUP BY t.${TeamSchema.teamId}
      ORDER BY g.${GameSchema.weekId} DESC
    """

  lazy val singleRowParser = 
    int(TeamSchema.teamId) ~
    str(TeamSchema.name) ~
    int(GameSchema.gameId) ~
    int(WeekSchema.weekId) ~
    long("Captures") map flatten

  lazy val multiRowParser = singleRowParser *

  def apply(x: (Int, String, Int, Int, Long)) : TeamGameResultRecord = 
    TeamGameResultRecord(x._1, x._2, x._3, x._4, x._5.toInt)
}

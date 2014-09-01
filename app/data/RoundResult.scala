package data

import anorm._ 
import anorm.SqlParser._
import AnormExtensions._

case class RoundResult(roundResultId: Int, roundId: Int, playerId: Int, captures: Int, pCaptures: Int, drops: Int, frags: Int, deaths: Int) {
  val fragPercentage : Double = if(frags > 0) deaths.toDouble / frags * 100.0 else 100.0
  val capturePercentage : Double = 0.0
  val pickupPercentage : Double = 0.0
  val stopPercentage : Double = 0.0
}

object RoundResult {
  lazy val removeRoundResult = 
    s"""
      DELETE FROM ${RoundResultSchema.tableName}
      WHERE ${RoundResultSchema.roundId} = {roundId}
    """

  lazy val updateRoundResult = 
    s"""
      UPDATE ${RoundResultSchema.tableName}
      SET
        ${RoundResultSchema.roundId} = {roundId},
        ${RoundResultSchema.playerId} = {playerId},
        ${RoundResultSchema.captures} = {captures},
        ${RoundResultSchema.pCaptures} = {pCaptures},
        ${RoundResultSchema.drops} = {drops},
        ${RoundResultSchema.frags} = {frags},
        ${RoundResultSchema.deaths} = {deaths}
      WHERE ${RoundResultSchema.roundResultId} = {roundResultId}
    """

  lazy val singleRowParser = 
    int(RoundResultSchema.roundResultId) ~
    int(RoundResultSchema.roundId) ~
    int(RoundResultSchema.playerId) ~
    int(RoundResultSchema.captures) ~
    int(RoundResultSchema.pCaptures) ~
    int(RoundResultSchema.drops) ~
    int(RoundResultSchema.frags) ~
    int(RoundResultSchema.deaths) map flatten

  lazy val multiRowParser = singleRowParser *

  def apply(x: (Int, Int, Int, Int, Int, Int, Int, Int)) : RoundResult = 
    RoundResult(x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8)
}

case class TeamGameRoundResultRecord(teamId: Int, teamName: String, gameId: Int, weekId: Int, gameTypeId: Int, roundId: Int, captures: Int)

object TeamGameRoundResultRecord {
  lazy val getRoundResultStatsBySeasonId = 
    s"""
      SELECT 
        t.TeamId,
        t.TeamName,
        g.GameId,
        g.WeekId,
        g.GameTypeId,
        r.RoundId,
        CAST(SUM(rr.Captures) AS SIGNED INT)AS TeamCaptures
        FROM Game AS g
        INNER JOIN Round AS r ON g.GameTypeId = 1 AND g.GameId = r.GameId
        INNER JOIN (
          SELECT DISTINCT
          tg.GameId
          FROM Season AS s
          INNER JOIN TeamSeason AS ts ON s.SeasonId = ts.SeasonId
          INNER JOIN Team AS t1 ON ts.TeamId = t1.TeamId
          INNER JOIN Team AS t2 ON ts.TeamId = t2.TeamId
          INNER JOIN TeamGame AS tg ON t1.TeamId = tg.Team1Id
            OR t2.TeamId = tg.Team2Id
          INNER JOIN TeamPlayer AS tp ON t1.TeamId = tp.TeamId 
            OR t2.TeamId = tp.TeamId
          WHERE (NULL IS NULL AND NOW() BETWEEN s.StartDate AND s.EndDate)
          OR (NULL IS NULL AND s.SeasonId = (
            SELECT SeasonId
            FROM Season
            ORDER BY StartDate DESC
            LIMIT 1))
          OR s.SeasonId = NULL
        ) AS filter ON g.GameId = filter.GameId
        INNER JOIN RoundResult AS rr ON r.IsEnabled = 1 AND r.RoundId = rr.RoundId
        INNER JOIN TeamPlayer AS tp ON rr.PlayerId = tp.PlayerId
        INNER JOIN Team AS t ON tp.TeamId = t.TeamId
      GROUP BY g.GameId, r.RoundId, t.TeamId
    """

  lazy val singleRowParser = 
    int(TeamSchema.teamId) ~
    str(TeamSchema.teamName) ~
    int(GameSchema.gameId) ~
    int(GameSchema.weekId) ~
    int(GameSchema.gameTypeId) ~
    int(RoundSchema.roundId) ~
    long("TeamCaptures") map flatten

  lazy val multiRowParser = singleRowParser *

  def apply(x: (Int, String, Int, Int, Int, Int, Long)) : TeamGameRoundResultRecord = 
    TeamGameRoundResultRecord(x._1, x._2, x._3, x._4, x._5, x._6, x._7.toInt)
}

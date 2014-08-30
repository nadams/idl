package data

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
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  lazy val getRoundResultStatsBySeasonId = 
    s"""
      SELECT 
        t.TeamId,
        t.Name,
        g.GameId,
        g.WeekId,
        g.GameTypeId,
        r.RoundId,
        rr.Captures
      FROM Game AS g
        INNER JOIN Round AS r on g.GameId = r.GameId
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
          WHERE ({seasonId} IS NULL AND NOW() BETWEEN s.StartDate AND s.EndDate)
            OR ({seasonId} IS NULL AND s.SeasonId = (
              SELECT SeasonId
              FROM Season
              ORDER BY StartDate DESC
              LIMIT 1))
            OR s.SeasonId = {seasonId}
        ) AS filter ON g.GameId = filter.GameId
        INNER JOIN RoundResult AS rr ON r.RoundId = rr.RoundId AND r.IsEnabled = 1
        INNER JOIN TeamPlayer AS tp ON rr.PlayerId = tp.PlayerId
        INNER JOIN Team AS t ON tp.TeamId = t.TeamId
      ORDER BY r.GameId, rr.RoundId
    """

  lazy val singleRowParser = 
    int(TeamSchema.teamId) ~
    str(TeamSchema.name) ~
    int(GameSchema.gameId) ~
    int(GameSchema.weekId) ~
    int(GameSchema.gameTypeId) ~
    int(RoundSchema.roundId) ~
    int(RoundResultSchema.captures) map flatten

  lazy val multiRowParser = singleRowParser *

  def apply(x: (Int, String, Int, Int, Int, Int, Int)) : TeamGameRoundResultRecord = 
    TeamGameRoundResultRecord(x._1, x._2, x._3, x._4, x._5, x._6, x._7)
}

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
        t.${TeamSchema.teamId},
        t.${TeamSchema.teamName},
        g.${GameSchema.gameId},
        g.${WeekSchema.weekId},
        g.${GameSchema.gameTypeId},
        r.${RoundSchema.roundId},
        CAST(SUM(rr.${RoundResultSchema.captures}) AS SIGNED INT) AS TeamCaptures
      FROM ${GameSchema.tableName} AS g
          INNER JOIN ${RoundSchema.tableName} AS r ON g.${GameSchema.gameTypeId} = ${GameTypes.Regular.id} AND g.${GameSchema.gameId} = r.${RoundSchema.gameId}
          INNER JOIN (
            SELECT DISTINCT
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
              OR ({seasonId} IS NULL AND s.${SeasonSchema.seasonId} = (
                SELECT ${SeasonSchema.seasonId}
                FROM ${SeasonSchema.tableName}
                ORDER BY ${SeasonSchema.startDate} DESC
                LIMIT 1))
              OR s.${SeasonSchema.seasonId} = NULL
          ) AS filter ON g.${GameSchema.gameId} = filter.${TeamGameSchema.gameId}
          INNER JOIN ${RoundResultSchema.tableName} AS rr ON r.${RoundSchema.isEnabled} = 1 AND r.${RoundSchema.roundId} = rr.${RoundResultSchema.roundId}
          INNER JOIN ${TeamPlayerSchema.tableName} AS tp ON rr.${RoundResultSchema.playerId} = tp.${TeamPlayerSchema.playerId}
          INNER JOIN ${TeamSchema.tableName} AS t ON tp.${TeamPlayerSchema.teamId} = t.${TeamSchema.teamId}
      GROUP BY g.${GameSchema.gameId}, r.${RoundSchema.roundId}, t.${TeamSchema.teamId}
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

package data

case class RoundStatsRecord(
  playerId: Int, 
  playerName: String, 
  teamId: Int,
  teamName: String,
  roundId: Int,
  gameId: Int,
  mapNumber: String,
  roundResultId: Int,
  captures: Int,
  pCaptures: Int,
  drops: Int,
  frags: Int,
  deaths: Int
)

object RoundStatsRecord {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  lazy val selectByGameSql = 
    s"""
      SELECT 
        p.${PlayerSchema.playerId},
        p.${PlayerSchema.playerName},
        t.${TeamSchema.teamId},
        t.${TeamSchema.teamName},
        r.${RoundSchema.roundId},
        r.${RoundSchema.gameId},
        r.${RoundSchema.mapNumber},
        rr.${RoundResultSchema.roundResultId},
        rr.${RoundResultSchema.captures},
        rr.${RoundResultSchema.pCaptures},
        rr.${RoundResultSchema.drops},
        rr.${RoundResultSchema.frags},
        rr.${RoundResultSchema.deaths}
      FROM ${RoundSchema.tableName} AS r
        INNER JOIN ${RoundResultSchema.tableName} AS rr ON r.${RoundSchema.roundId} = rr.${RoundResultSchema.roundId}
        INNER JOIN ${TeamPlayerSchema.tableName} AS tp ON rr.${RoundResultSchema.playerId} = tp.${TeamPlayerSchema.playerId}
        INNER JOIN ${PlayerSchema.tableName} AS p ON tp.${TeamPlayerSchema.playerId} = p.${PlayerSchema.playerId}
        INNER JOIN ${TeamSchema.tableName} AS t ON tp.${TeamPlayerSchema.teamId} = t.${TeamSchema.teamId}
      WHERE r.${RoundSchema.isEnabled} = 1 
        AND r.${RoundSchema.gameId} = {gameId}
        AND ({playerId} IS NULL OR rr.${RoundResultSchema.playerId} = {playerId})
      ORDER BY r.${RoundSchema.roundId}, p.${PlayerSchema.playerName} ASC
    """

  lazy val singleRowParser = 
    int(PlayerSchema.playerId) ~
    str(PlayerSchema.playerName) ~
    int(TeamSchema.teamId) ~
    str(TeamSchema.teamName) ~
    int(RoundSchema.roundId) ~
    int(RoundSchema.gameId) ~
    str(RoundSchema.mapNumber) ~
    int(RoundResultSchema.roundResultId) ~
    int(RoundResultSchema.captures) ~
    int(RoundResultSchema.pCaptures) ~
    int(RoundResultSchema.drops) ~
    int(RoundResultSchema.frags) ~
    int(RoundResultSchema.deaths) map flatten

  lazy val multiRowParser = singleRowParser *

  def apply(x: (Int, String, Int, String, Int, Int, String, Int, Int, Int, Int, Int, Int)) : RoundStatsRecord = 
    RoundStatsRecord(x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11, x._12, x._13)
}

package data 

case class FellowPlayerRecord(playerId: Int, playerName: String, teamId: Int, teamName: String)

object FellowPlayerRecord {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  lazy val selectByPlayerId =
    s"""
      SELECT 
        teams.${PlayerSchema.playerId}, 
        teams.${PlayerSchema.playerName},
        t.${TeamSchema.teamId}, 
        t.${TeamSchema.teamName}
      FROM ${PlayerSchema.tableName} AS pl 
        INNER JOIN ${TeamPlayerSchema.tableName} AS tp ON pl.${PlayerSchema.playerId} = tp.${TeamPlayerSchema.playerId}
        INNER JOIN ${TeamSchema.tableName} AS t ON tp.${TeamPlayerSchema.teamId} = t.${TeamSchema.teamId} AND t.${TeamSchema.isActive} = 1 
        INNER JOIN ${PlayerProfileSchema.tableName} AS plp ON pl.${PlayerSchema.playerId} = plp.${PlayerProfileSchema.playerId} AND ${PlayerProfileSchema.isApproved} = 1
        INNER JOIN ${ProfileSchema.tableName} AS p ON plp.${PlayerProfileSchema.profileId} = p.${ProfileSchema.profileId}
        INNER JOIN (
          SELECT 
            t2.${TeamSchema.teamId},
            pl2.${PlayerSchema.playerId},
            pl2.${PlayerSchema.playerName}
          FROM ${TeamSchema.tableName} AS t2 
          INNER JOIN ${TeamPlayerSchema.tableName} AS tp2 ON t2.${TeamSchema.teamId} = tp2.${TeamPlayerSchema.teamId}
          INNER JOIN ${PlayerSchema.tableName} AS pl2 ON tp2.${TeamPlayerSchema.playerId} = pl2.${PlayerSchema.playerId} AND pl2.${PlayerSchema.playerId}
          LEFT OUTER JOIN ${PlayerProfileSchema.tableName} AS pp ON pl2.${PlayerSchema.playerId} = pp.${PlayerProfileSchema.playerId} AND pp.${PlayerProfileSchema.isApproved} = 1
        ) AS teams ON t.${TeamSchema.teamId} = teams.${TeamSchema.teamId} 
      WHERE p.${ProfileSchema.profileId} = {profileId}
    """

  lazy val singleRowParser =
    int(PlayerSchema.playerId) ~
    str(PlayerSchema.playerName) ~
    int(TeamSchema.teamId) ~
    str(TeamSchema.teamName) map flatten

  lazy val multiRowParser = singleRowParser *

  def apply(x: (Int, String, Int, String)) : FellowPlayerRecord = 
    FellowPlayerRecord(x._1, x._2, x._3, x._4)
}


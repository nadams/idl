package data

case class TeamPlayerRecord(playerId: Int, playerName: String, teamId: Option[Int], isCaptain: Option[Boolean])

object TeamPlayerRecord {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._
  
  lazy val selectAll = 
    s"""
      SELECT 
        p.${PlayerSchema.playerId},
        p.${PlayerSchema.playerName},
        tp.${TeamPlayerSchema.teamId},
        tp.${TeamPlayerSchema.isCaptain}
      FROM ${PlayerSchema.tableName} AS p 
        LEFT OUTER JOIN ${TeamPlayerSchema.tableName} AS tp ON p.${PlayerSchema.playerId} = tp.${TeamPlayerSchema.playerId}
    """
    
  lazy val singleRowParser = 
    int(PlayerSchema.playerId) ~
    str(PlayerSchema.playerName) ~
    get[Option[Int]](TeamPlayerSchema.teamId) ~
    get[Option[Boolean]](TeamPlayerSchema.isCaptain) map flatten 
    
  lazy val multiRowParser = singleRowParser *

  def apply(x: (Int, String, Option[Int], Option[Boolean])): TeamPlayerRecord =
    TeamPlayerRecord(x._1, x._2, x._3, x._4)
}

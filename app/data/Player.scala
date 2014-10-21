package data

case class Player(playerId: Int, playerName: String, isActive: Boolean, teamId: Option[Int])

object Player {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  lazy val selectByProfileId = 
    s"""
      SELECT 
        p.${PlayerSchema.playerId},
        p.${PlayerSchema.playerName},
        p.${PlayerSchema.isActive}
      FROM ${PlayerSchema.tableName} AS p
        INNER JOIN ${PlayerProfileSchema.tableName} AS pp ON p.${PlayerSchema.playerId} = pp.${PlayerProfileSchema.playerId}
          AND pp.${PlayerProfileSchema.profileId} = {profileId}
    """

  lazy val insertPlayer =
    s"""
      INSERT INTO ${PlayerSchema.tableName} (
        ${PlayerSchema.playerName},
        ${PlayerSchema.isActive}
      ) VALUES (
        {playerName},
        {isActive}
      )
    """

  lazy val singleRowParser =
    int(PlayerSchema.playerId) ~
    str(PlayerSchema.playerName) ~
    bool(PlayerSchema.isActive) map flatten

  lazy val multiRowParser = singleRowParser *

  def apply(x: (Int, String, Boolean, Option[Int])) : Player = Player(x._1, x._2, x._3, x._4)
  def apply(x: (Int, String, Boolean)) : Player = Player(x._1, x._2, x._3, None)
}

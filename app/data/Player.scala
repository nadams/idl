package data

import org.joda.time.DateTime

case class Player(playerId: Int, playerName: String, isActive: Boolean, dateCreated: DateTime, teamId: Option[Int])

object Player {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  lazy val selectPlayer =
    s"""
      SELECT 
        p.${PlayerSchema.playerId},
        p.${PlayerSchema.playerName},
        p.${PlayerSchema.isActive},
        p.${PlayerSchema.dateCreated},
        NULL AS ${TeamPlayerSchema.teamId}
      FROM ${PlayerSchema.tableName} AS p
      WHERE ${PlayerSchema.playerId} = {playerId}
    """

  lazy val selectAllPlayersSql = 
    s"""
      SELECT 
        p.${PlayerSchema.playerId},
        p.${PlayerSchema.playerName},
        p.${PlayerSchema.isActive},
        p.${PlayerSchema.dateCreated},
        tp.${TeamPlayerSchema.teamId}
      FROM ${PlayerSchema.tableName} AS p
        LEFT OUTER JOIN ${TeamPlayerSchema.tableName} AS tp ON p.${PlayerSchema.playerId} = tp.${TeamPlayerSchema.playerId}
    """

  lazy val selectByProfileId = 
    s"""
      SELECT 
        p.${PlayerSchema.playerId},
        p.${PlayerSchema.playerName},
        p.${PlayerSchema.isActive},
        p.${PlayerSchema.dateCreated}
      FROM ${PlayerSchema.tableName} AS p
        INNER JOIN ${PlayerProfileSchema.tableName} AS pp ON p.${PlayerSchema.playerId} = pp.${PlayerProfileSchema.playerId}
          AND pp.${PlayerProfileSchema.profileId} = {profileId}
    """

  lazy val selectByPlayerName =
    s"""
      $selectAllPlayersSql
      WHERE p.${PlayerSchema.playerName} = {playerName}
    """

  lazy val insertPlayer =
    s"""
      INSERT INTO ${PlayerSchema.tableName} (
        ${PlayerSchema.playerName},
        ${PlayerSchema.isActive},
        ${PlayerSchema.dateCreated}
      ) VALUES (
        {playerName},
        {isActive},
        {dateCreated}
      )
    """

  lazy val singleRowParser =
    int(PlayerSchema.playerId) ~
    str(PlayerSchema.playerName) ~
    bool(PlayerSchema.isActive) ~
    get[DateTime](PlayerSchema.dateCreated) map flatten

  lazy val multiRowParser = singleRowParser *

  def apply(x: (Int, String, Boolean, DateTime, Option[Int])) : Player = Player(x._1, x._2, x._3, x._4, x._5)
  def apply(x: (Int, String, Boolean, DateTime)) : Player = Player(x._1, x._2, x._3, x._4, None)
}

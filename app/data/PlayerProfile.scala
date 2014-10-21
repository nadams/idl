package data

case class PlayerProfile(profileId: Int, playerId: Int, isApproved: Boolean)

object PlayerProfile {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  lazy val removePlayerFromProfileSql = 
    s"""
      DELETE FROM ${PlayerProfileSchema.tableName}
      WHERE ${PlayerProfileSchema.playerId} = {playerId}
        AND ${PlayerProfileSchema.profileId} = {profileId}
    """

  lazy val playerIsInAnyProfileSql =
    s"""
      SELECT COUNT(*)
      FROM ${PlayerProfileSchema.tableName}
      WHERE ${PlayerProfileSchema.playerId} = {playerId}
    """

  lazy val insertPlayerProfileSql =
    s"""
      INSERT INTO ${PlayerProfileSchema.tableName} (
        ${PlayerProfileSchema.playerId},
        ${PlayerProfileSchema.profileId}
      ) VALUES (
        {playerId},
        {profileId}
      )
    """

  lazy val selectByProfileIdAndPlayerId = 
    s"""
      SELECT 
        ${PlayerProfileSchema.playerId},
        ${PlayerProfileSchema.profileId},
        ${PlayerProfileSchema.isApproved}
      FROM ${PlayerProfileSchema.tableName} 
      WHERE ${PlayerProfileSchema.playerId} = {playerId}
        AND ${PlayerProfileSchema.profileId} = {profileId}
    """

  lazy val singleRowParser = 
    int(PlayerProfileSchema.playerId) ~
    int(PlayerProfileSchema.profileId) ~
    bool(PlayerProfileSchema.isApproved) map flatten

  def apply(x: (Int, Int, Boolean)) : PlayerProfile = 
    PlayerProfile(x._1, x._2, x._3)
}

package data

import anorm._ 
import anorm.SqlParser._
import AnormExtensions._

case class PlayerProfile(profileId: Int, playerId: Int, isApproved: Boolean)

object PlayerProfile {
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
        ${PlayerProfileSchema.profileId},
        ${PlayerProfileSchema.playerId},
        ${PlayerProfileSchema.isApproved}
      FROM ${PlayerProfileSchema.tableName} 
      WHERE ${PlayerProfileSchema.playerId} = {playerId}
        AND ${PlayerProfileSchema.profileId} = {profileId}
    """

  lazy val singleRowParser = 
    int(PlayerProfileSchema.profileId) ~
    int(PlayerProfileSchema.playerId) ~
    bool(PlayerProfileSchema.isApproved) map flatten

  def apply(x: (Int, Int, Boolean)) : PlayerProfile = 
    PlayerProfile(x._1, x._2, x._3)
}

case class PlayerProfileRecord(playerId: Int, profileId: Int, playerName: String, isApproved: Boolean)

object PlayerProfileRecord {
  lazy val selectByProfileId = 
    s"""
      SELECT 
        pp.${PlayerProfileSchema.playerId},
        pp.${PlayerProfileSchema.profileId},
        p.${PlayerSchema.playerName},
        pp.${PlayerProfileSchema.isApproved}
      FROM ${PlayerProfileSchema.tableName} AS pp 
        INNER JOIN ${PlayerSchema.tableName} AS p ON pp.${PlayerProfileSchema.playerId} = p.${PlayerSchema.playerId}
      WHERE pp.${PlayerProfileSchema.profileId} = {profileId}
    """
    
  lazy val selectByProfileIdAndPlayerId = 
    s"""
      $selectByProfileId
        AND pp.${PlayerProfileSchema.playerId} = {playerId}
    """

  lazy val singleRowParser = 
    int(PlayerProfileSchema.playerId) ~
    int(PlayerProfileSchema.profileId) ~
    str(PlayerSchema.playerName) ~
    bool(PlayerProfileSchema.isApproved) map flatten

  lazy val multiRowParser = singleRowParser *

  def apply(x: (Int, Int, String, Boolean)) : PlayerProfileRecord = 
    PlayerProfileRecord(x._1, x._2, x._3, x._4)
}

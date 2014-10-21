package data

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
}

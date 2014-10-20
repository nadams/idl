package data

object PlayerProfile {
  lazy val removePlayerFromProfileSql = 
    s"""
      DELETE FROM ${PlayerProfileSchema.tableName}
      WHERE ${PlayerProfileSchema.playerId} = {playerId}
        AND ${PlayerProfileSchema.profileId} = {profileId}
    """
}

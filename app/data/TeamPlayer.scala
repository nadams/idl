package data

object TeamPlayer {
  lazy val insertTeamPlayer =
    s"""
      INSERT INTO ${TeamPlayerSchema.tableName} (
        ${TeamPlayerSchema.teamId},
        ${TeamPlayerSchema.playerId}, 
        ${TeamPlayerSchema.isCaptain},
        ${TeamPlayerSchema.isApproved}
      ) VALUES(
        {teamId}, 
        {playerId}, 
        {isCaptain},
        {isApproved}
      )
    """

  lazy val removeTeamPlayer =
    s"""
      DELETE FROM 
        ${TeamPlayerSchema.tableName}
      WHERE 
        ${TeamPlayerSchema.playerId} = {playerId} AND 
        ${TeamPlayerSchema.teamId} = {teamId}
    """
}

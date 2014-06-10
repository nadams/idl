package data

trait TeamRepositoryComponent {
  val teamRepository: TeamRepository

  trait TeamRepository {
    def getTeamsForSeason(seasonId: Int) : Seq[Team]
    def assignPlayerToTeam(playerId: Int, teamId: Int, isCaptain: Boolean = false) : Boolean
    def removePlayerFromTeam(playerId: Int, teamId: Int) : Boolean
    def updateTeamPlayer(playerId: Int, teamId: Int, isCaptain: Boolean) : Boolean
    def getAllActiveTeams() : Seq[Team]
  }
}

trait TeamRepositoryComponentImpl extends TeamRepositoryComponent {
  val teamRepository = new TeamRepositoryImpl

  class TeamRepositoryImpl extends TeamRepository {
    import anorm._ 
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current

    val selectAllTeamsSql = 
      s"""
        SELECT
          t.${TeamSchema.teamId},
          t.${TeamSchema.name},
          t.${TeamSchema.isActive} 
        FROM ${TeamSchema.tableName} AS t
      """
    val teamParser = int(TeamSchema.teamId) ~ str(TeamSchema.name) ~ bool(TeamSchema.isActive) map flatten
    val multiRowParser = teamParser *

    def getTeamsForSeason(seasonId: Int) = DB.withConnection { implicit connection =>
      SQL(
        s"""
          $selectAllTeamsSql
            INNER JOIN ${TeamSeasonSchema.tableName} AS tp ON t.${TeamSchema.teamId} = tp.${TeamSeasonSchema.teamId}
          WHERE ${TeamSeasonSchema.seasonId} = {seasonId}
        """
      )
      .on('seasonId -> seasonId)
      .as(multiRowParser)
      .map(Team(_))
    }

    def assignPlayerToTeam(playerId: Int, teamId: Int, isCaptain: Boolean = false) = DB.withConnection { implicit connection =>
      SQL(
        s"""
          SELECT COUNT(*) AS Results
          FROM ${TeamPlayerSchema.tableName}
          WHERE
            ${TeamPlayerSchema.playerId} = {playerId} AND
            ${TeamPlayerSchema.teamId} = {teamId}
        """
      )
      .on(
        'playerId -> playerId,
        'teamId -> teamId
      )
      .as(scalar[Long] single) match {
        case x if x == 0 => SQL(
          s"""
            INSERT INTO ${TeamPlayerSchema.tableName} (
              ${TeamPlayerSchema.teamId},
              ${TeamPlayerSchema.playerId}, 
              ${TeamPlayerSchema.isCaptain}
            ) VALUES(
              {teamId}, 
              {playerId}, 
              {isCaptain}
            )
          """
        )
        .on(
          'teamId -> teamId,
          'playerId -> playerId,
          'isCaptain -> isCaptain
        )
        .executeUpdate > 0
        case _ => false
      }
    }

    def removePlayerFromTeam(playerId: Int, teamId: Int) : Boolean = DB.withConnection { implicit collection => 
      SQL(
        s"""
          DELETE FROM 
            ${TeamPlayerSchema.tableName}
          WHERE 
            ${TeamPlayerSchema.playerId} = {playerId} AND 
            ${TeamPlayerSchema.teamId} = {teamId}
        """
      )
      .on(
        'playerId -> playerId,
        'teamId -> teamId
      )
      .executeUpdate > 0
    }

    def updateTeamPlayer(playerId: Int, teamId: Int, isCaptain: Boolean) : Boolean = DB.withConnection { implicit connection => 
      SQL(
        s"""
          UPDATE ${TeamPlayerSchema.tableName}
          SET ${TeamPlayerSchema.isCaptain} = {isCaptain}
          WHERE 
            ${TeamPlayerSchema.playerId} = {playerId} AND 
            ${TeamPlayerSchema.teamId} = {teamId}
        """
      )
      .on(
        'playerId -> playerId,
        'teamId -> teamId,
        'isCaptain -> isCaptain
      )
      .executeUpdate > 0
    }

    def getAllActiveTeams() : Seq[Team] = DB.withConnection { implicit connection => 
      SQL(
        s"""
          $selectAllTeamsSql
          WHERE t.${TeamSchema.isActive} = 1
        """
      )
      .as(multiRowParser)
      .map(Team(_))
    }
  }
}

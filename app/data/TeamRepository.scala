package data

trait TeamRepositoryComponent {
  val teamRepository: TeamRepository

  trait TeamRepository {
    def getTeamsForSeason(seasonId: Int) : Seq[Team]
    def assignPlayerToTeam(playerId: Int, teamId: Int, seasonId: Int, isCaptain: Boolean = false) : Boolean
  }
}

trait TeamRepositoryComponentImpl extends TeamRepositoryComponent {
  trait TeamSchema {
    val tableName = "Team"
    val teamId = "TeamId"
    val name = "Name"
    val isActive = "IsActive"
    val teamPlayerTableName = "TeamPlayer"
    val teamPlayerPlayerId = "PlayerId"
    val teamPlayerSeasonId = "SeasonId"
    val teamPlayerTeamId = "TeamId"
    val teamPlayerIsCaptain = "IsCaptain"
  }

  val teamRepository = new TeamRepositoryImpl

  class TeamRepositoryImpl extends TeamRepository with TeamSchema {
    import anorm._ 
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current

    val teamParser = int(teamId) ~ str(name) ~ bool(isActive) map flatten
    val multiRowParser = teamParser *

    def getTeamsForSeason(seasonId: Int) = DB.withConnection { implicit connection =>
      SQL(
        s"""
          SELECT
            t.$teamId,
            t.$name,
            t.$isActive 
          FROM $tableName AS t
            INNER JOIN $teamPlayerTableName AS tp ON t.$teamId = tp.$teamPlayerPlayerId
          WHERE $teamPlayerSeasonId = {seasonId}
        """
      )
      .on('seasonId -> seasonId)
      .as(multiRowParser)
      .map(Team(_))
    }

    def assignPlayerToTeam(playerId: Int, teamId: Int, seasonId: Int, isCaptain: Boolean = false) = DB.withConnection { implicit connection =>
      SQL(
        s"""
          INSERT INTO $teamPlayerTableName (
            $teamPlayerTeamId, 
            $teamPlayerPlayerId, 
            $teamPlayerSeasonId, 
            $teamPlayerIsCaptain
          ) VALUES(
            {teamId}, 
            {playerId}, 
            {seasonId}, 
            {isCaptain}
          )
        """
      )
      .on(
        'teamId -> teamId,
        'playerId -> playerId,
        'seasonId -> seasonId,
        'isCaptain -> isCaptain
      )
      .executeUpdate > 0
    }
  }
}
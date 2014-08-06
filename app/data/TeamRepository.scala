package data

trait TeamRepositoryComponent {
  val teamRepository: TeamRepository

  trait TeamRepository {
    def getTeamsForSeason(seasonId: Int) : Seq[Team]
    def assignPlayerToTeam(playerId: Int, teamId: Int, isCaptain: Boolean = false) : Boolean
    def removePlayerFromTeam(playerId: Int, teamId: Int) : Boolean
    def updateTeamPlayer(playerId: Int, teamId: Int, isCaptain: Boolean) : Boolean
    def getAllActiveTeams() : Seq[Team]
    def insertTeam(team: Team) : Boolean
    def updateTeam(team: Team) : Boolean
    def getTeam(teamId: Int) : Option[Team]
    def getAllTeams() : Seq[Team]
    def removeTeam(teamId: Int) : Boolean
    def getTeamsForGame(gameId: Int) : Option[(Team, Team)]
  }
}

trait TeamRepositoryComponentImpl extends TeamRepositoryComponent {
  val teamRepository = new TeamRepositoryImpl

  class TeamRepositoryImpl extends TeamRepository {
    import anorm._ 
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    import org.joda.time.DateTime
    import AnormExtensions._

    val selectAllTeamsSql = 
      s"""
        SELECT
          ${teamProjection("t")}
        FROM ${TeamSchema.tableName} AS t
      """

    val teamParser = 
      int(TeamSchema.teamId) ~ 
      str(TeamSchema.name) ~ 
      bool(TeamSchema.isActive) ~ 
      datetime(TeamSchema.dateCreated) map flatten

    val multiRowParser = teamParser *

    def teamProjection(alias: String) = 
      s"""
        $alias.${TeamSchema.teamId},
        $alias.${TeamSchema.name},
        $alias.${TeamSchema.isActive} ,
        $alias.${TeamSchema.dateCreated}
      """

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

    def insertTeam(team: Team) : Boolean = DB.withConnection { implicit connection => 
      SQL(
        s"""
          INSERT INTO ${TeamSchema.tableName} (
            ${TeamSchema.name},
            ${TeamSchema.isActive},
            ${TeamSchema.dateCreated}
          ) VALUES (
            {teamName},
            {isActive},
            {dateCreated}
          )
        """
      )
      .on(
        'teamName -> team.name,
        'isActive -> team.isActive,
        'dateCreated -> team.dateCreated
      )
      .executeUpdate > 0
    } 

    def updateTeam(team: Team) : Boolean = DB.withConnection { implicit connection => 
      SQL(
        s"""
          UPDATE ${TeamSchema.tableName}
          SET
            ${TeamSchema.name} = {teamName},
            ${TeamSchema.isActive} = {isActive}
          WHERE
            ${TeamSchema.teamId} = {teamId}
        """
      )
      .on(
        'teamId -> team.teamId,
        'teamName -> team.name,
        'isActive -> team.isActive,
        'dateCreated -> team.dateCreated
      )
      .executeUpdate > 0
    }

    def getTeam(teamId: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          $selectAllTeamsSql
          WHERE ${TeamSchema.teamId} = {teamId}
        """
      )
      .on(
        'teamId -> teamId
      )
      .as(teamParser singleOpt)
      .map(Team(_))
    }

    def getAllTeams() = DB.withConnection { implicit connection => 
      SQL(selectAllTeamsSql)
      .as(multiRowParser)
      .map(Team(_))
    }

    def removeTeam(teamId: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          DELETE FROM ${TeamSchema.tableName}
          WHERE ${TeamSchema.teamId} = {teamId}
        """
      )
      .on(
        'teamId -> teamId
      )
      .executeUpdate > 0
    }

    def getTeamsForGame(gameId: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          SELECT 
            t1.${TeamSchema.teamId} AS Team1Id,
            t1.${TeamSchema.name} AS Team1Name,
            t1.${TeamSchema.isActive} AS Team1IsActive,
            t1.${TeamSchema.dateCreated} AS Team1DateCreated,
            t2.${TeamSchema.teamId} AS Team2Id,
            t2.${TeamSchema.name} AS Team2Name,
            t2.${TeamSchema.isActive} AS Team2IsActive,
            t2.${TeamSchema.dateCreated} AS Team2DateCreated
          FROM ${TeamGameSchema.tableName} AS tg
            INNER JOIN ${TeamSchema.tableName} AS t1 on tg.${TeamGameSchema.team1Id} = t1.${TeamSchema.teamId}
            INNER JOIN ${TeamSchema.tableName} AS t2 on tg.${TeamGameSchema.team2Id} = t2.${TeamSchema.teamId}
          WHERE ${TeamGameSchema.gameId} = {gameId}
        """
      )
      .on('gameId -> gameId)
      .as(
        int("Team1Id") ~
        str("Team1Name") ~
        bool("Team1IsActive") ~
        datetime("Team1DateCreated") ~
        int("Team2Id") ~
        str("Team2Name") ~
        bool("Team2IsActive") ~
        datetime("Team2DateCreated") map flatten singleOpt
      )
      .map { data => (
        Team(data._1, data._2, data._3, data._4), 
        Team(data._5, data._6, data._7, data._8)
      )}
    }
  }
}

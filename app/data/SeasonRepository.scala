package data

trait SeasonRepositoryComponent {
  val seasonRepository: SeasonRepository

  trait SeasonRepository {
    def getAllSeasons() : Seq[Season]
    def getSeasonById(id: Int) : Option[Season]
    def insertSeason(season: Season) : Boolean
    def updateSeason(season: Season) : Boolean
    def removeSeason(id: Int) : Boolean
    def removeTeamFromSeason(seasonId: Int, teamId: Int) : Boolean
    def assignTeamToSeason(seasonId: Int, teamId: Int) : Boolean
  }
}

trait SeasonRepositoryComponentImpl extends SeasonRepositoryComponent {
  val seasonRepository: SeasonRepository = new SeasonRepositoryImpl

  class SeasonRepositoryImpl extends SeasonRepository {
    import java.sql._
    import anorm._ 
    import anorm.SqlParser._
    import org.joda.time.DateTime
    import play.api.db.DB
    import play.api.Play.current
    import AnormExtensions._

    val seasonParser = int(SeasonSchema.seasonId) ~ 
      str(SeasonSchema.name) ~ 
      datetime(SeasonSchema.startDate) ~ 
      datetime(SeasonSchema.endDate) map flatten
      
    val multiRowParser = seasonParser *
    val selectAllNewsSql = 
      s"""
        SELECT
          ${SeasonSchema.seasonId},
          ${SeasonSchema.name},
          ${SeasonSchema.startDate},
          ${SeasonSchema.endDate}
        FROM ${SeasonSchema.tableName}
      """

    def getAllSeasons() : Seq[Season] = DB.withConnection { implicit connection => 
      SQL(
        s"""
          ${selectAllNewsSql}
          ORDER BY ${SeasonSchema.startDate}, ${SeasonSchema.endDate} DESC
        """
      )
      .as(multiRowParser)
      .map(Season(_))
    }

    def getSeasonById(id: Int) : Option[Season] = DB.withConnection { implicit connection => 
      SQL(
        s"""
          $selectAllNewsSql
          WHERE ${SeasonSchema.seasonId} = {seasonId}
        """
      )
      .on('seasonId -> id)
      .as(seasonParser singleOpt)
      .map(Season(_))
    }

    def insertSeason(season: Season) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          INSERT INTO ${SeasonSchema.tableName} (
            ${SeasonSchema.name}, 
            ${SeasonSchema.startDate}, 
            ${SeasonSchema.endDate}
          ) VALUES (
            {name},
            {startDate},
            {endDate}
          )
        """
      )
      .on(
        'name -> season.name,
        'startDate -> season.startDate,
        'endDate -> season.endDate
      )
      .executeInsert(scalar[Long] single) > 0
    }

    def updateSeason(season: Season) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          UPDATE ${SeasonSchema.tableName}
          SET
            ${SeasonSchema.name} = {name},
            ${SeasonSchema.startDate} = {startDate},
            ${SeasonSchema.endDate} = {endDate}
          WHERE ${SeasonSchema.seasonId} = {seasonId}
        """
      )
      .on(
        'seasonId -> season.seasonId,
        'name -> season.name,
        'startDate -> season.startDate,
        'endDate -> season.endDate
      )
      .executeUpdate > 0
    }

    def removeSeason(id: Int) = DB.withTransaction { implicit connection => 
      val removeFromTeamSeason = SQL(
        s"""
          DELETE FROM ${TeamSeasonSchema.tableName}
          WHERE ${TeamSeasonSchema.seasonId} = {seasonId};
        """
      )
      .on('seasonId -> id)
      .executeUpdate > 0
      
      val removeFromSeason = SQL(
        s"""
          DELETE FROM ${SeasonSchema.tableName}
          WHERE ${SeasonSchema.seasonId} = {seasonId};
        """
      )
      .on('seasonId -> id)
      .executeUpdate > 0

      removeFromTeamSeason || removeFromSeason
    }

    def removeTeamFromSeason(seasonId: Int, teamId: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          DELETE FROM 
            ${TeamSeasonSchema.tableName}
          WHERE 
            ${TeamSeasonSchema.seasonId} = {seasonId} AND 
            ${TeamSeasonSchema.teamId} = {teamId}
        """
      )
      .on(
        'seasonId -> seasonId,
        'teamId -> teamId
      )
      .executeUpdate > 0
    }

    def assignTeamToSeason(seasonId: Int, teamId: Int) : Boolean = DB.withConnection { implicit connection => 
      SQL(
        s"""
          INSERT INTO ${TeamSeasonSchema.tableName} (
            ${TeamSeasonSchema.seasonId},
            ${TeamSeasonSchema.teamId}
          ) VALUES (
            {seasonId},
            {teamId}
          )
        """
      )
      .on(
        'seasonId -> seasonId,
        'teamId -> teamId
      )
      .executeUpdate > 0
    }
  }
}
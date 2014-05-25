package data

trait SeasonRepositoryComponent {
  val seasonRepository: SeasonRepository

  trait SeasonRepository {
    def getAllSeasons() : Seq[Season]
    def getSeasonById(id: Int) : Option[Season]
    def insertSeason(season: Season) : Boolean
    def updateSeason(season: Season) : Boolean
    def removeSeason(id: Int) : Boolean
  }
}

trait SeasonRepositoryComponentImpl extends SeasonRepositoryComponent {
  val seasonRepository: SeasonRepository = new SeasonRepositoryImpl

  trait SeasonSchema {
    val tableName = "Season"
    val seasonId = "SeasonId"
    val name = "Name"
    val startDate = "StartDate"
    val endDate = "EndDate"
  }

  class SeasonRepositoryImpl extends SeasonRepository with SeasonSchema {
    import java.sql._
    import anorm._ 
    import anorm.SqlParser._
    import org.joda.time.DateTime
    import play.api.db.DB
    import play.api.Play.current
    import AnormExtensions._

    val seasonParser = int(seasonId) ~ str(name) ~ get[DateTime](startDate) ~ get[DateTime](endDate) map flatten
    val multiRowParser = seasonParser *
    val selectAllNewsSql = 
      s"""
        SELECT
          $seasonId,
          $name,
          $startDate,
          $endDate
        FROM $tableName
      """

    def getAllSeasons() : Seq[Season] = DB.withConnection { implicit connection => 
      SQL(selectAllNewsSql)
      .as(multiRowParser)
      .map(Season(_))
    }

    def getSeasonById(id: Int) : Option[Season] = DB.withConnection { implicit connection => 
      SQL(
        s"""
          $selectAllNewsSql
          WHERE $seasonId = {seasonId}
        """
      )
      .on("seasonId" -> id)
      .singleOpt(seasonParser)
      .map(Season(_))
    }

    def insertSeason(season: Season) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          INSERT INTO $tableName ($name, $startDate, $endDate)
          VALUES (
            {name},
            {startDate},
            {endDate}
          )
        """
      )
      .on(
        "name" -> season.name,
        "startDate" -> season.startDate,
        "endDate" -> season.endDate
      )
      .executeInsert(scalar[Long] single) > 0
    }

    def updateSeason(season: Season) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          UPDATE $tableName
          SET
            $name = {name},
            $startDate = {startDate},
            $endDate = {endDate}
          WHERE $seasonId = {seasonId}
        """
      )
      .on(
        "seasonId" -> season.seasonId,
        "name" -> season.name,
        "startDate" -> season.startDate,
        "endDate" -> season.endDate
      )
      .executeUpdate > 0
    }

    def removeSeason(id: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          DELETE FROM $tableName
          WHERE $seasonId = {seasonId}
        """
      )
      .on("seasonId" -> id)
      .executeUpdate > 0
    }
  }
}
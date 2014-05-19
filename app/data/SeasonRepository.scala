package data

trait SeasonRepositoryComponent {
  val seasonRepository: SeasonRepository

  trait SeasonRepository {
    def getAllSeasons() : Seq[Season]
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
  }
}
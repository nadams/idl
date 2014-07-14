package data

trait GameRepositoryComponent {
  val gameRepository: GameRepository

  trait GameRepository {
    def getGamesBySeasonId(seasonId: Int) : Seq[Game]
  }
}

trait GameRepositoryComponentImpl extends GameRepositoryComponent {
  val gameRepository = new GameRepositoryImpl

  class GameRepositoryImpl extends GameRepository {
    import java.sql._
    import anorm._ 
    import anorm.SqlParser._
    import org.joda.time.{ DateTime, DateTimeZone }
    import play.api.db.DB
    import play.api.Play.current
    import AnormExtensions._

    val selectAllGamesSql = 
      s"""
        SELECT
          g.${GameSchema.gameId},
          g.${GameSchema.weekId},
          g.${GameSchema.seasonId},
          g.${GameSchema.scheduledPlayTime},
          g.${GameSchema.dateCompleted},
          tg.${TeamGameSchema.team1},
          tg.${TeamGameSchema.team2}
        FROM ${GameSchema.tableName} AS g
          LEFT OUTER JOIN ${TeamGameSchema.tableName} as tg on g.${GameSchema.gameId} = tg.${TeamGameSchema.gameId}
      """

    val gameParser = 
      int(GameSchema.gameId) ~
      int(GameSchema.weekId) ~
      int(GameSchema.seasonId) ~
      get[DateTime](GameSchema.scheduledPlayTime) ~
      get[DateTime](GameSchema.dateCompleted) ~
      get[Option[Int]](TeamGameSchema.team1) ~ 
      get[Option[Int]](TeamGameSchema.team2) map flatten

    val multiRowParser = gameParser *

    def getGamesBySeasonId(seasonId: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          $selectAllGamesSql
          WHERE g.${GameSchema.seasonId} = {seasonId}
        """
      )
      .on('seasonId -> seasonId)
      .as(multiRowParser)
      .map(Game(_))
    }
  }
}

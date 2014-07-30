package data

trait GameRepositoryComponent {
  val gameRepository: GameRepository

  trait GameRepository {
    def getGamesBySeasonId(seasonId: Int) : Seq[Game]
    def getGamesForProfile(username: String) : Seq[Game]
    def insert(game: Game) : Boolean
    def addGameResults(gameId: Int, data: Seq[(String, GameResult)]) : Unit
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
          tg.${TeamGameSchema.team1Id},
          tg.${TeamGameSchema.team2Id}
        FROM ${GameSchema.tableName} AS g
          LEFT OUTER JOIN ${TeamGameSchema.tableName} as tg on g.${GameSchema.gameId} = tg.${TeamGameSchema.gameId}
      """

    val gameParser = 
      int(GameSchema.gameId) ~
      int(GameSchema.weekId) ~
      int(GameSchema.seasonId) ~
      get[DateTime](GameSchema.scheduledPlayTime) ~
      get[Option[DateTime]](GameSchema.dateCompleted) ~
      get[Option[Int]](TeamGameSchema.team1Id) ~ 
      get[Option[Int]](TeamGameSchema.team2Id) map flatten

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

    def getGamesForProfile(username: String) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          SELECT 
            g.${GameSchema.gameId},
            g.${GameSchema.weekId},
            g.${GameSchema.seasonId},
            g.${GameSchema.scheduledPlayTime},
            g.${GameSchema.dateCompleted}
          FROM ${ProfileSchema.tableName} AS p
            INNER JOIN ${PlayerProfileSchema.tableName} AS pp ON p.${ProfileSchema.profileId} = pp.${PlayerProfileSchema.profileId}
            INNER JOIN ${PlayerSchema.tableName} AS p2 ON pp.${PlayerProfileSchema.playerId} = p2.${PlayerSchema.playerId}
            INNER JOIN ${TeamPlayerSchema.tableName} AS tp ON p2.${PlayerSchema.playerId} = tp.${TeamPlayerSchema.playerId}
            INNER JOIN ${TeamSchema.tableName} AS t ON tp.${TeamPlayerSchema.teamId} = t.${TeamSchema.teamId}
            INNER JOIN ( 
              SELECT 
                ${TeamGameSchema.gameId} AS GameId, 
                ${TeamGameSchema.team1Id} AS TeamId
              FROM ${TeamGameSchema.tableName}

              UNION ALL
              
              SELECT 
                ${TeamGameSchema.gameId} AS GameId, 
                ${TeamGameSchema.team2Id} AS TeamId 
              FROM ${TeamGameSchema.tableName}
            ) AS tg ON t.${TeamSchema.teamId} = tg.TeamId
            INNER JOIN ${GameSchema.tableName} AS g on tg.GameId = g.${GameSchema.gameId}
          WHERE p.${ProfileSchema.email} = {email}
          ORDER BY g.${GameSchema.scheduledPlayTime} DESC
        """
      )
      .on('email -> username)
      .as(multiRowParser)
      .map(Game(_))
    }

    def insert(game: Game) = DB.withTransaction { implicit connection => 
      val gameId = SQL(
        s"""
          INSERT INTO ${GameSchema.tableName} (
            ${GameSchema.weekId},
            ${GameSchema.seasonId},
            ${GameSchema.scheduledPlayTime},
            ${GameSchema.dateCompleted}
          ) VALUES (
            {weekId},
            {seasonId},
            {scheduledPlayTime},
            {dateCompleted}
          )
        """
      ).on(
        'weekId -> game.weekId,
        'seasonId -> game.seasonId,
        'scheduledPlayTime -> game.scheduledPlayTime,
        'dateCompleted -> game.dateCompleted
      ).executeInsert(scalar[Long] single).toInt

      val success = if(gameId > 0) {
        game.teams.map { teams => 
          SQL(
            s"""
              INSERT INTO ${TeamGameSchema.tableName} (
                ${TeamGameSchema.gameId},
                ${TeamGameSchema.team1Id},
                ${TeamGameSchema.team2Id}
              ) VALUES (
                {gameId},
                {team1Id},
                {team2Id}
              )
            """
          ).on(
            'gameId -> gameId,
            'team1Id -> teams._1,
            'team2Id -> teams._2
          ).executeUpdate > 0
        }.getOrElse(true)
      } else false

      success
    }

    def addGameResults(gameId: Int, data: Seq[(String, GameResult)]) = DB.withTransaction { implicit connection => 
      data.foreach { x => 
        val playerName = x._1
        val gameResult = x._2

        val playerId = SQL(
          s"""
            SELECT ${PlayerSchema.playerId}
            FROM ${PlayerSchema.tableName}
            WHERE ${PlayerSchema.name} = {playerName}
          """
        )
        .on('playerName -> playerName)
        .as(scalar[Int] singleOpt)
        .get //This is dangerous, but since we're in a transaction, 
             //we want an exception to be thrown if an error occurs.

        SQL(
          s"""
            INSERT INTO ${GameResultSchema.tableName} (
              ${GameResultSchema.gameId},
              ${GameResultSchema.playerId},
              ${GameResultSchema.captures},
              ${GameResultSchema.pCaptures},
              ${GameResultSchema.drops},
              ${GameResultSchema.frags},
              ${GameResultSchema.deaths}
            ) VALUES (
              {gameId},
              {playerId},
              {captures},
              {pCaptures},
              {drops},
              {frags},
              {deaths}
            )
          """
        )
        .on(
          'gameId -> gameId,
          'playerId -> playerId,
          'captures -> gameResult.captures,
          'pCaptures -> gameResult.pCaptures,
          'drops -> gameResult.drops,
          'frags -> gameResult.frags,
          'deaths -> gameResult.deaths
        )
        .executeUpdate
      } 
    }
  }
}

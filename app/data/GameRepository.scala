package data

import java.io.File

trait GameRepositoryComponent {
  val gameRepository: GameRepository

  trait GameRepository {
    def getGame(gameId: Int) : Option[Game]
    def getGamesBySeasonId(seasonId: Int) : Seq[Game]
    def getGamesForProfile(username: String) : Seq[Game]
    def addGame(game: Game) : Boolean
    def updateGame(game: Game) : Boolean
    def removeGame(gameId: Int) : Boolean
    def addGameResults(gameId: Int, data: Seq[(String, GameResult)]) : Unit
    def gameHasResults(gameId: Int) : Boolean
    def getGameResults(gameId: Int) : Map[String, GameResult]
    def getDemoStatusForGame(gameId: Int) : Seq[DemoStatusRecord]
    def addDemo(gameId: Int, playerId: Int, file: File) : Option[GameDemo]
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

    val gameDemoParser = 
      int(GameDemoSchema.gameDemoId) ~
      int(GameDemoSchema.gameId) ~
      int(GameDemoSchema.playerId) ~
      str(GameDemoSchema.filename) ~
      get[DateTime](GameDemoSchema.dateUploaded) ~
      str(PlayerSchema.name) map flatten

    val multiRowParser = gameParser *
    val gameDemoMultiRow = gameDemoParser *

    def getGame(gameId: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          $selectAllGamesSql
          WHERE g.${GameSchema.gameId} = {gameId}
        """
      )
      .on('gameId -> gameId)
      .as(gameParser singleOpt)
      .map(Game(_))
    }

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

    def addGame(game: Game) = DB.withTransaction { implicit connection => 
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

    def updateGame(game: Game) = DB.withTransaction { implicit connection => 
      val updateGameSuccess = SQL(
        s"""
          UPDATE ${GameSchema.tableName}
          SET
            ${GameSchema.weekId} = {weekId},
            ${GameSchema.seasonId} = {seasonId},
            ${GameSchema.scheduledPlayTime} = {scheduledPlayTime},
            ${GameSchema.dateCompleted} = {dateCompleted}
          WHERE ${GameSchema.gameId} = {gameId}
        """
      ).on(
        'gameId -> game.gameId,
        'weekId -> game.weekId,
        'seasonId -> game.seasonId,
        'scheduledPlayTime -> game.scheduledPlayTime,
        'dateCompleted -> game.dateCompleted
      ).executeUpdate > 0

      if(updateGameSuccess) {
        game.teams.map { teams => 
          SQL(
            s"""
              UPDATE ${TeamGameSchema.tableName}
              SET
                ${TeamGameSchema.team1Id} = {team1Id},
                ${TeamGameSchema.team2Id} = {team2Id}
              WHERE
                ${TeamGameSchema.gameId} = {gameId}
            """
          ).on(
            'gameId -> game.gameId,
            'team1Id -> teams._1,
            'team2Id -> teams._2
          ).executeUpdate > 0
        } getOrElse(true)
      } else {
        false
      }
    }

    def removeGame(gameId: Int) = DB.withTransaction { implicit connection => 
      SQL(
        s"""
          DELETE FROM ${TeamGameSchema.tableName}
          WHERE ${TeamGameSchema.gameId} = {gameId}
        """
      ).on('gameId -> gameId)
      .executeUpdate

      SQL(
        s"""
          DELETE FROM ${GameSchema.tableName}
          WHERE ${GameSchema.gameId} = {gameId}
        """
      ).on('gameId -> gameId)
      .executeUpdate > 0
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

    def gameHasResults(gameId: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          SELECT COUNT(*)
          FROM ${GameResultSchema.tableName}
          WHERE ${GameResultSchema.gameId} = {gameId}
        """
      ).on('gameId -> gameId)
      .as(scalar[Long] single) > 0
    }

    def getGameResults(gameId: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          SELECT
            gr.${GameResultSchema.gameResultId},
            gr.${GameResultSchema.gameId},
            gr.${GameResultSchema.playerId},
            gr.${GameResultSchema.captures},
            gr.${GameResultSchema.pCaptures},
            gr.${GameResultSchema.drops},
            gr.${GameResultSchema.frags},
            gr.${GameResultSchema.deaths},
            p.${PlayerSchema.name}
          FROM ${GameResultSchema.tableName} AS gr
            INNER JOIN ${PlayerSchema.tableName} AS p ON gr.${GameResultSchema.playerId} = ${PlayerSchema.playerId}
          WHERE ${GameResultSchema.gameId} = {gameId}
        """
      ).on('gameId -> gameId)
      .as(
        int(GameResultSchema.gameResultId) ~
        int(GameResultSchema.gameId) ~ 
        int(GameResultSchema.playerId) ~
        int(GameResultSchema.captures) ~
        int(GameResultSchema.pCaptures) ~
        int(GameResultSchema.drops) ~
        int(GameResultSchema.frags) ~
        int(GameResultSchema.deaths) ~ 
        str(PlayerSchema.name) map flatten *
      ).map(x => (x._9, GameResult(x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8)))
      .toMap
    }

    def getDemoStatusForGame(gameId: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          SELECT 
            p.${PlayerSchema.playerId},
            p.${PlayerSchema.name},
            gd.${GameDemoSchema.gameDemoId},
            gd.${GameDemoSchema.filename},
            gd.${GameDemoSchema.dateUploaded}
          FROM ${PlayerSchema.tableName} AS p 
            INNER JOIN ${TeamPlayerSchema.tableName} AS tp on p.${PlayerSchema.playerId} = tp.${TeamPlayerSchema.playerId}
            INNER JOIN ${GameResultSchema.tableName} AS gr on p.${PlayerSchema.playerId} = gr.${GameResultSchema.playerId}
            INNER JOIN ${GameSchema.tableName} AS g on gr.${GameResultSchema.gameId} = g.${GameSchema.gameId}
            LEFT OUTER JOIN ${GameDemoSchema.tableName} AS gd on gr.${GameResultSchema.gameId} = gd.${GameDemoSchema.gameId}
              AND gd.${GameDemoSchema.playerId} = p.${PlayerSchema.playerId}
          WHERE gr.${GameResultSchema.gameId} = {gameId}
        """
      ).on('gameId -> gameId)
      .as(DemoStatusRecord.multiRowParser)
      .map(DemoStatusRecord(_))
    }

    def addDemo(gameId: Int, playerId: Int, file: File) = DB.withConnection { implicit connection => 
      import java.nio.file.{ Files, Paths }

      val data : scala.Array[Byte] = Files.readAllBytes(Paths.get(file.getAbsolutePath))
      val now = new DateTime(DateTimeZone.UTC)
      val gameDemoId = SQL(
        s"""
          INSERT INTO ${GameDemoSchema.tableName} (
            ${GameDemoSchema.gameId},
            ${GameDemoSchema.playerId},
            ${GameDemoSchema.filename},
            ${GameDemoSchema.dateUploaded},
            ${GameDemoSchema.demoFile}
          )
          VALUES (
            {gameId},
            {playerId},
            {filename},
            {date},
            {data}
          )
        """
      ).on(
        'gameId -> gameId,
        'playerId -> playerId,
        'filename -> file.getName,
        'date -> now,
        'data -> data
      ).executeInsert(scalar[Long] single).toInt

      if(gameDemoId > 0)
        Some(GameDemo(
          gameDemoId,
          gameId,
          playerId,
          file.getName,
          now,
          ""
        ))
      else None
    }
  }
}

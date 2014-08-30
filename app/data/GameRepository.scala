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
    def getDemoStatusForGame(gameId: Int) : Seq[DemoStatusRecord]
    def addDemo(gameId: Int, playerId: Int, filename: String, file: File) : Option[GameDemo]
    def getGameDemoByPlayerAndGame(gameId: Int, playerId: Int) : Option[GameDemo]
    def getDemoData(gameDemoId: Int) : Option[Array[Byte]]
    def getTeamGameResults(seasonId: Option[Int]) : Seq[TeamGameResultRecord]
    def addRound(gameId: Int, mapNumber: String) : Option[Round]
    def hasRoundResults(gameId: Int) : Boolean
    def addRoundResult(roundId: Int, data: (String, RoundResult)) : RoundResult
    def getRoundStats(gameId: Int) : Seq[RoundStatsRecord]
    def disableRound(round: Round) : Option[Round]
    def getRound(roundId: Int) : Option[Round]
    def getTeamGameRoundResults(seasonId: Option[Int]) : Seq[TeamGameRoundResultRecord]
    def updateRoundResult(roundResult: RoundResult) : Option[Round]
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

    def getGame(gameId: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          ${Game.selectAllSql}
          WHERE g.${GameSchema.gameId} = {gameId}
        """
      )
      .on('gameId -> gameId)
      .as(Game.singleRowParser singleOpt)
      .map(Game(_))
    }

    def getGamesBySeasonId(seasonId: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          ${Game.selectAllSql}
          WHERE g.${GameSchema.seasonId} = {seasonId}
        """
      )
      .on('seasonId -> seasonId)
      .as(Game.multiRowParser)
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
      .as(Game.multiRowParser)
      .map(Game(_))
    }

    def addGame(game: Game) = DB.withTransaction { implicit connection => 
      val gameId = SQL(
        s"""
          INSERT INTO ${GameSchema.tableName} (
            ${GameSchema.weekId},
            ${GameSchema.seasonId},
            ${GameSchema.gameTypeId},
            ${GameSchema.scheduledPlayTime},
            ${GameSchema.dateCompleted}
          ) VALUES (
            {weekId},
            {seasonId},
            {gameTypeId},
            {scheduledPlayTime},
            {dateCompleted}
          )
        """
      ).on(
        'weekId -> game.weekId,
        'seasonId -> game.seasonId,
        'gameTypeId -> game.gameTypeId,
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
            ${GameSchema.gameTypeId} = {gameTypeId},
            ${GameSchema.scheduledPlayTime} = {scheduledPlayTime},
            ${GameSchema.dateCompleted} = {dateCompleted}
          WHERE ${GameSchema.gameId} = {gameId}
        """
      ).on(
        'gameId -> game.gameId,
        'weekId -> game.weekId,
        'seasonId -> game.seasonId,
        'gameTypeId -> game.gameTypeId,
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
            INNER JOIN ${TeamPlayerSchema.tableName} AS tp ON p.${PlayerSchema.playerId} = tp.${TeamPlayerSchema.playerId}
            INNER JOIN ${TeamGameSchema.tableName} AS tg ON tp.${TeamPlayerSchema.teamId} = tg.${TeamGameSchema.team1Id} 
              OR tp.${TeamPlayerSchema.teamId} = tg.${TeamGameSchema.team2Id}
            LEFT OUTER JOIN ${GameDemoSchema.tableName} AS gd ON tg.${GameSchema.gameId} = gd.${GameDemoSchema.gameId}
              AND gd.${GameDemoSchema.playerId} = p.${PlayerSchema.playerId}
          WHERE tg.${TeamGameSchema.gameId} = {gameId}
        """
      ).on('gameId -> gameId)
      .as(DemoStatusRecord.multiRowParser)
      .map(DemoStatusRecord(_))
    }

    def addDemo(gameId: Int, playerId: Int, filename: String, file: File) = DB.withConnection { implicit connection => 
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
        'filename -> filename,
        'date -> now,
        'data -> data
      ).executeInsert(scalar[Long] single).toInt

      if(gameDemoId > 0)
        Some(GameDemo(
          gameDemoId,
          gameId,
          playerId,
          filename,
          now
        ))
      else None
    }

    def getGameDemoByPlayerAndGame(gameId: Int, playerId: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          ${GameDemo.selectAllSql}
          WHERE ${GameDemoSchema.gameId} = {gameId}
            AND ${GameDemoSchema.playerId} = {playerId}
        """
      ).on(
        'gameId -> gameId,
        'playerId -> playerId
      ).as(GameDemo.singleRowParser singleOpt)
      .map(GameDemo(_))
    }

    def getDemoData(gameDemoId: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          SELECT ${GameDemoSchema.demoFile}
          FROM ${GameDemoSchema.tableName}
          WHERE ${GameDemoSchema.gameDemoId} = {gameDemoId}
        """
      ).on('gameDemoId -> gameDemoId)
      .as(bytes(GameDemoSchema.demoFile) singleOpt)
    }

    def getTeamGameResults(seasonId: Option[Int]) = DB.withConnection { implicit connection => 
      SQL(TeamGameResultRecord.selectResultsBySeasonId)
      .on('seasonId -> seasonId)
      .as(TeamGameResultRecord.multiRowParser)
      .map(TeamGameResultRecord(_))
    }

    def addRound(gameId: Int, mapNumber: String) = DB.withConnection { implicit connection => 
      SQL(Round.insertRound)
        .on(
          'gameId -> gameId,
          'mapNumber -> mapNumber
        )
        .executeInsert(scalar[Long] singleOpt)
        .map(x => Round(x.toInt, gameId, mapNumber, true))
    }

    def hasRoundResults(gameId: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          SELECT COUNT(*)
          FROM ${RoundSchema.tableName} AS r 
            INNER JOIN ${RoundResultSchema.tableName} AS rs ON r.${RoundSchema.roundId} = rs.${RoundResultSchema.roundId}
          WHERE ${RoundSchema.gameId} = {gameId}
        """
      ).on('gameId -> gameId)
      .as(scalar[Long] single) > 0      
    }

    def addRoundResult(roundId: Int, data: (String, RoundResult)) = DB.withConnection { implicit connection =>
      val playerName = data._1
      val roundResult = data._2

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

      val roundResultId = SQL(
        s"""
          INSERT INTO ${RoundResultSchema.tableName} (
            ${RoundResultSchema.roundId},
            ${RoundResultSchema.playerId},
            ${RoundResultSchema.captures},
            ${RoundResultSchema.pCaptures},
            ${RoundResultSchema.drops},
            ${RoundResultSchema.frags},
            ${RoundResultSchema.deaths}
          ) VALUES (
            {roundId},
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
        'roundId -> roundId,
        'playerId -> playerId,
        'captures -> roundResult.captures,
        'pCaptures -> roundResult.pCaptures,
        'drops -> roundResult.drops,
        'frags -> roundResult.frags,
        'deaths -> roundResult.deaths
      )
      .executeUpdate

      roundResult.copy(roundResultId = roundResultId)
    }

    def getRoundStats(gameId: Int) = DB.withConnection { implicit connection => 
      SQL(RoundStatsRecord.selectByGameSql)
      .on('gameId -> gameId)
      .as(RoundStatsRecord.multiRowParser)
      .map(RoundStatsRecord(_))
    }

    def getRoundStat

    def disableRound(round: Round) = DB.withTransaction { implicit connection => 
      if(SQL(Round.removeRound).on('roundId -> round.roundId).executeUpdate > 0) Some(round.copy(isEnabled = false))
      else None
    }

    def getRound(roundId: Int) = DB.withConnection { implicit connection => 
      SQL(Round.selectRound)
      .on('roundId -> roundId)
      .as(Round.singleRowParser singleOpt)
      .map(Round(_))
    }

    def getTeamGameRoundResults(seasonId: Option[Int]) = DB.withConnection { implicit connection => 
      SQL(TeamGameRoundResultRecord.getRoundResultStatsBySeasonId)
      .on('seasonId -> seasonId)
      .as(TeamGameRoundResultRecord.multiRowParser)
      .map(TeamGameRoundResultRecord(_))
    }

    def updateRoundResult(roundResult: RoundResult) = DB.withConnection { implicit connection => 
      try {
        SQL(RoundResult.updateRoundResult)
        .on(
          'roundResultId = roundResult.roundResultId,
          'roundId = roundResult.roundId,
          'playerId = roundResult.playerId,
          'captures = roundResult.captures,
          'pCaptures = roundResult.pCaptures,
          'drops = roundResult.drops,
          'frags = roundResult.frgs,
          'deaths = roundResult.deaths
        ).executeUpdate
      } catch {
        case e: Exception => None
      }

      Some(roundResult)
    }

    // def updateDemo(gameId: Int, playerId: Int, filename: String, file: File) = DB.withConnection { implicit connection => 
    //   import java.nio.file.{ Files, Paths }

    //   val data : scala.Array[Byte] = Files.readAllBytes(Paths.get(file.getAbsolutePath))
    //   val now = new DateTime(DateTimeZone.UTC)
    //   val result = SQL(
    //     s"""
    //       UPDATE ${GameDemoSchema.tableName}
    //         SET ${GameDemoSchema.filename} = {filename},
    //         SET ${GameDemoSchema.dateUploaded} = {date},
    //         SET ${GameDemoSchema.demoFile} = {data}
    //       WHERE ${GameDemoSchema.gameId} = {gameId}
    //         AND ${GameDemoSchema.playerId} = {playerId}
    //     """
    //   ).on(
    //     'gameId -> gameId,
    //     'playerId -> playerId,
    //     'filename -> filename,
    //     'date -> now,
    //     'data -> data
    //   ).executeUpdate > 0

    //   if(result > 0)
    //     Some(GameDemo(
    //       gameDemoId,
    //       gameId,
    //       playerId,
    //       filename,
    //       now,
    //       ""
    //     ))
    // }
  }
}

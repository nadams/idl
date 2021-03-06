package services

import scala.io.{ Source, Codec }
import java.io.File
import org.joda.time.{ DateTime, DateTimeZone }
import data._

trait GameServiceComponent {
  val gameService: GameService

  trait GameService {
    def getGame(gameId: Int) : Option[Game]
    def getGamesBySeasonId(seasonId: Int) : Seq[Game]
    def getGamesForProfile(username: String) : Seq[Game]
    def addGame(game: Game) : Boolean
    def updateGame(game: Game) : Boolean
    def removeGame(gameId: Int) : Boolean
    def parseGameResults(gameId: Int, source: Source) : Seq[(String, Seq[(String, RoundResult)])]
    def addRoundResults(gameId: Int, data: Seq[(String, Seq[(String, RoundResult)])]) : Unit
    def getDemoStatusForGame(gameId: Int) : Seq[DemoStatusRecord]
    def addDemo(gameId: Int, playerId: Int, filename: String, file: File) : Option[GameDemo]
    def getDemoData(demoDataId: Int) : Option[Array[Byte]]
    def addRound(gameId: Int, mapNumber: String) : Option[Round]
    def getRound(roundId: Int) : Option[Round]
    def getRoundStats(gameId: Int) : Seq[RoundStatsRecord]
    def getRoundStatsForPlayer(gameId: Int, roundId: Int, playerId: Int) : Option[RoundStatsRecord]
    def disableRound(round: Round) : Option[Round]
    def getTeamGameRoundResults(seasonId: Option[Int]) : Seq[TeamGameRoundResultRecord]
    def updateRoundResult(roundResult: RoundResult) : Option[RoundResult]
  }
}

trait GameServiceComponentImpl extends GameServiceComponent {
  self: GameRepositoryComponent =>
  val gameService = new GameServiceImpl

  class GameServiceImpl extends GameService {
    def getGame(gameId: Int) = gameRepository.getGame(gameId)
    def getGamesBySeasonId(seasonId: Int) = gameRepository.getGamesBySeasonId(seasonId)
    def getGamesForProfile(username: String) = gameRepository.getGamesForProfile(username)
    def addGame(game: Game) = gameRepository.addGame(game)
    def updateGame(game: Game) = gameRepository.updateGame(game)
    def removeGame(gameId: Int) = gameRepository.removeGame(gameId)
    def getDemoStatusForGame(gameId: Int) = gameRepository.getDemoStatusForGame(gameId)
    def addDemo(gameId: Int, playerId: Int, filename: String, file: File) = gameRepository.addDemo(gameId, playerId, filename, file)
    def getDemoData(demoDataId: Int) = gameRepository.getDemoData(demoDataId)
    def addRound(gameId: Int, mapNumber: String) = gameRepository.addRound(gameId, mapNumber)
    def getRoundStats(gameId: Int) = gameRepository.getRoundStats(gameId)
    def disableRound(round: Round) = gameRepository.disableRound(round)
    def getRound(roundId: Int) = gameRepository.getRound(roundId)
    def getTeamGameRoundResults(seasonId: Option[Int]) = gameRepository.getTeamGameRoundResults(seasonId)
    def updateRoundResult(roundResult: RoundResult) = gameRepository.updateRoundResult(roundResult)
    def getRoundStatsForPlayer(gameId: Int, roundId: Int, playerId: Int) = gameRepository.getRoundStatsForPlayer(gameId, roundId, playerId)

    def parseGameResults(gameId: Int, source: Source) = 
      ZandronumLogParser.parseLog(source).map { case (roundName, playerStats) => 
        roundName -> playerStats.filter(_._2.team != Teams.Spectator).keys.map { playerName => 
          val value = playerStats(playerName)

          playerName -> RoundResult(0, gameId,  0, value.captures, value.pCaptures, value.drops, value.frags, value.deaths)
        }.toSeq
      }

    def addRoundResults(gameId: Int, data: Seq[(String, Seq[(String, RoundResult)])]) = 
      if(!gameRepository.hasRoundResults(gameId)) {
        gameRepository.getGame(gameId).map { game => 
          data.foreach { item => 
            gameRepository.addRound(game.gameId, item._1).map { round => 
              item._2.map(gameRepository.addRoundResult(round.roundId, _))
              gameRepository.updateGame(game.copy(dateCompleted = Some(new DateTime(DateTimeZone.UTC))))            
            }
          }
        }
      } 
  }
}

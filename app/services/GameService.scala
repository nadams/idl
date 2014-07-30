package services

import scala.io.{ Source, Codec }
import data.{ GameRepositoryComponent, Game, GameResult }

trait GameServiceComponent {
  val gameService: GameService

  trait GameService {
    def getGamesBySeasonId(seasonId: Int) : Seq[Game]
    def getGamesForProfile(username: String) : Seq[Game]
    def addGame(game: Game) : Boolean
    def addGameResult(gameId: Int, data: Array[Byte]) : Unit
  }
}

trait GameServiceComponentImpl extends GameServiceComponent {
  self: GameRepositoryComponent =>
  val gameService = new GameServiceImpl

  class GameServiceImpl extends GameService {
    def getGamesBySeasonId(seasonId: Int) = gameRepository.getGamesBySeasonId(seasonId)
    def getGamesForProfile(username: String) = gameRepository.getGamesForProfile(username)
    def addGame(game: Game) = gameRepository.insert(game)
    def addGameResult(gameId: Int, data: Array[Byte]) = {
      val source = Source.fromBytes(data)(Codec.ISO8859)
      val playerStats = ZandronumLogParser.parseLog(source)
      val stats = playerStats.keys.map { key => 
        val value = playerStats(key)

        (key, GameResult(0, gameId, 0, value.captures, value.pCaptures, value.drops, value.frags, value.deaths))
      } toSeq

      gameRepository.addGameResults(gameId, stats)
    }
  }
}

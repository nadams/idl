package services

import data.{ GameRepositoryComponent, Game }

trait GameServiceComponent {
  val gameService: GameService

  trait GameService {
    def getGamesBySeasonId(seasonId: Int) : Seq[Game]
  }
}

trait GameServiceComponentImpl extends GameServiceComponent {
  self: GameRepositoryComponent =>
  val gameService = new GameServiceImpl

  class GameServiceImpl extends GameService {
    def getGamesBySeasonId(seasonId: Int) = gameRepository.getGamesBySeasonId(seasonId)
  }
}

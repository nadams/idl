package data

trait GameRepositoryComponent {
  val gameRepository: GameRepository

  trait GameRepository {
    
  }
}

trait GameRepositoryComponentImpl {
  val gameRepository = new GameRepositoryImpl

  class GameRepositoryImpl extends GameRepository {

  }
}

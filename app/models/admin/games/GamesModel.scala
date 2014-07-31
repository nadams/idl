package models.admin.games

import org.joda.time.DateTime
import data._

case class GamesModel(games: Seq[GameModel])
case class GameModel(
  gameId: Int, 
  team1: String, 
  team2: String,
  scheduledWeek: String, 
  scheduledTime: DateTime,
  status: GameStatus.Value,
  gameStatus: String,
  removeLink: String,
  editLink: String,
  resultsLink: String
)

object GamesModel {
  def empty = GamesModel(Seq.empty[GameModel])

  def toModel(seasonId: Int, games: Seq[(Game, Option[(Team, Team)])], routes: controllers.ReverseGameController) = 
    GamesModel(games.map(teamGame => GameModel.toModel(seasonId, teamGame._1, teamGame._2, routes)))
}

object GameModel {
  def toModel(seasonId: Int, game: Game, teams: Option[(Team, Team)], routes: controllers.ReverseGameController) = {
    val teamNames = teams.map(teams => (teams._1.name, teams._2.name)).getOrElse(("", ""))
    val status = game.dateCompleted.map(date => "Completed").getOrElse("Pending")

    GameModel(
      game.gameId, 
      teamNames._1, 
      teamNames._2, 
      Weeks(game.weekId).toString, 
      game.scheduledPlayTime, 
      game.status,
      status, 
      routes.remove(seasonId, game.gameId).url, 
      routes.edit(seasonId, game.gameId).url,
      routes.stats(seasonId, game.gameId).url
    )
  }
}

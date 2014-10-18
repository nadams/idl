package models.admin.games

import org.joda.time.DateTime
import data._
import play.api.libs.json.Json

case class GamesModel(games: Seq[GameModel])
case class GameModel(
  gameId: Int, 
  team1: String, 
  team2: String,
  gameType: String,
  scheduledWeek: String, 
  scheduledTime: DateTime,
  status: String,
  gameStatus: String,
  removeLink: String,
  editLink: String,
  resultsLink: String
)

object GamesModel {
  implicit val writesGameModel = Json.writes[GameModel]
  implicit val writesGamesModel = Json.writes[GamesModel]
  
  def empty = GamesModel(Seq.empty[GameModel])

  def toModel(seasonId: Int, games: Seq[(Game, Option[(Team, Team)])], routes: controllers.ReverseGameController) = 
    GamesModel(games.map(teamGame => GameModel.toModel(seasonId, teamGame._1, teamGame._2, routes)))
}

object GameModel {
  def toModel(seasonId: Int, game: Game, teams: Option[(Team, Team)], routes: controllers.ReverseGameController) = {
    val teamNames = teams.map(teams => (teams._1.teamName, teams._2.teamName)).getOrElse(("", ""))
    val status = game.dateCompleted.map(date => "Completed").getOrElse("Pending")

    GameModel(
      game.gameId, 
      teamNames._1, 
      teamNames._2, 
      GameTypes(game.gameTypeId).toString,
      Weeks(game.weekId).toString, 
      game.scheduledPlayTime, 
      game.status.toString,
      status, 
      routes.remove(seasonId, game.gameId).url, 
      routes.edit(seasonId, game.gameId).url,
      routes.stats(seasonId, game.gameId).url
    )
  }
}

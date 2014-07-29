package models.admin.games

import org.joda.time.DateTime
import data.{ Game, Weeks, Team }

case class GamesModel(games: Seq[GameModel])
case class GameModel(
  gameId: Int, 
  team1: String, 
  team2: String,
  scheduledWeek: String, 
  scheduledTime: DateTime,
  gameStatus: String,
  removeLink: String
)

object GamesModel {
  def empty = GamesModel(Seq.empty[GameModel])

  def toModel(games: Seq[(Game, Option[(Team, Team)])]) = GamesModel(
    games.map(teamGame => GameModel.toModel(teamGame._1, teamGame._2))
  )
}

object GameModel {
  def toModel(game: Game, teams: Option[(Team, Team)]) = {
    val teamNames = teams.map(teams => (teams._1.name, teams._2.name)).getOrElse(("", ""))
    val status = game.dateCompleted.map(date => "Completed").getOrElse("Pending")

    GameModel(game.gameId, teamNames._1, teamNames._2, Weeks(game.weekId).toString, game.scheduledPlayTime, status, "")
  }
}

package models.admin.seasons

import org.joda.time.DateTime
import data.{ Game, Weeks }

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

  def toModel(games: Seq[Game]) = GamesModel(games.map(GameModel.toModel(_)))
}

object GameModel {
  def toModel(game: Game) = GameModel(game.gameId, "", "", Weeks(game.weekId).toString, game.scheduledPlayTime, "", "")
}

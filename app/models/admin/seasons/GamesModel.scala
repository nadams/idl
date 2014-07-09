package models.admin.seasons

import data.Game

case class GamesModel(games: Seq[Game])
case class GameModel()

object GamesModel {
  def empty = GamesModel(Seq.empty[Game])
}

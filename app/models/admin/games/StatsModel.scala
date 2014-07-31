package models.admin.games

import play.api.libs.json.Json

case class StatsModel(seasonId: Int, gameId: Int, stats: Option[GameStatsModel]) {
  val hasUploadedStats = stats.isDefined
}

object StatsModel {
  implicit val writesGameStatsModel = Json.writes[GameStatsModel]
  implicit val writesStatsModel = Json.writes[StatsModel]

  def empty = StatsModel(0, 0, None)
}

case class GameStatsModel(test: Int)

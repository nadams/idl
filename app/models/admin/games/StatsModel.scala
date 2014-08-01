package models.admin.games

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.json.Json.JsValueWrapper

case class StatsModel(seasonId: Int, gameId: Int, stats: Seq[GameDemoModel])
case class GameDemoModel(playerId: String, playerName: String, demoData: Option[DemoData])
case class DemoData(filename: String, dateUploaded: String)

object StatsModel {
  implicit val writesDemoData = Json.writes[DemoData]
  implicit val writesGameDemoModel = Json.writes[GameDemoModel]
  implicit val writesStatsModel = Json.writes[StatsModel]

  def empty = StatsModel(0, 0, Seq.empty[GameDemoModel])
}

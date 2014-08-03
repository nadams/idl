package models.admin.games

import play.api.libs.json.Json
import _root_.data._

case class StatsModel(seasonId: Int, gameId: Int, demoUploaded: Boolean, stats: Seq[GameDemoModel])
case class GameDemoModel(playerId: String, playerName: String, demoData: Option[DemoData])
case class DemoData(filename: String, dateUploaded: String)

object StatsModel {
  implicit val writesDemoData = Json.writes[DemoData]
  implicit val writesGameDemoModel = Json.writes[GameDemoModel]
  implicit val writesStatsModel = Json.writes[StatsModel]

  def empty = StatsModel(0, 0, false, Seq.empty[GameDemoModel])

  def toModel(seasonId: Int, gameId: Int, demoUploaded: Boolean, demos: Seq[GameDemo]) : StatsModel = 
    StatsModel(
      seasonId,
      gameId,
      demoUploaded,
      demos.map(GameDemoModel.toModel(_))
    )
}

object GameDemoModel {
  def toModel(demo: GameDemo) : GameDemoModel = ???
}

object DemoData {
  def toModel(gameDemo: GameDemo) : DemoData = ???
}

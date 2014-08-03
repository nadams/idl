package models.admin.games

import play.api.libs.json.Json
import org.joda.time.format.ISODateTimeFormat
import com.github.nscala_time.time.Imports._
import formatters.DateTimeFormatter
import _root_.data._


case class StatsModel(seasonId: Int, gameId: Int, statsUploaded: Boolean, stats: Seq[GameDemoModel])

object StatsModel {
  implicit val writesDemoData = Json.writes[DemoData]
  implicit val writesGameDemoModel = Json.writes[GameDemoModel]
  implicit val writesStatsModel = Json.writes[StatsModel]

  def empty = StatsModel(0, 0, false, Seq.empty[GameDemoModel])

  def toModel(seasonId: Int, gameId: Int, statsUploaded: Boolean, demos: Seq[DemoStatusRecord]) : StatsModel = 
    StatsModel(
      seasonId,
      gameId,
      statsUploaded,
      demos.map(GameDemoModel.toModel(_))
    )
}

case class GameDemoModel(playerId: Int, playerName: String, demoData: Option[DemoData])
object GameDemoModel {
  def toModel(demo: DemoStatusRecord) = GameDemoModel(
    demo.playerId,
    demo.playerName,
    demo.demoDetails.map(DemoData.toModel(_))
  )
}

case class DemoData(gameDemoId: Int, filename: String, dateUploaded: DateTime)
object DemoData {
  def toModel(gameDemo: DemoDetailsRecord) = DemoData(
    gameDemo.gameDemoId,
    gameDemo.filename,
    gameDemo.dateUploaded
  )
}

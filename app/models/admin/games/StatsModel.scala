package models.admin.games

import play.api.libs.json.Json
import org.joda.time.DateTime
import formatters.DateTimeFormatter._
import data._

case class StatsModel(seasonId: Int, gameId: Int, statsUploaded: Boolean, demoInfo: Seq[GameDemoModel], rounds: Seq[RoundModel])

object StatsModel {
  implicit val readsRoundStatsData = Json.reads[RoundStatsModel]
  implicit val writesRoundStatsData = Json.writes[RoundStatsModel]
  implicit val writesRoundData = Json.writes[RoundModel]
  implicit val writesDemoData = Json.writes[DemoData]
  implicit val writesGameDemoModel = Json.writes[GameDemoModel]
  implicit val writesStatsModel = Json.writes[StatsModel]

  def empty = StatsModel(0, 0, false, Seq.empty[GameDemoModel], Seq.empty[RoundModel])

  def toModel(seasonId: Int, game: Game, demos: Seq[DemoStatusRecord], roundStats: Seq[RoundStatsRecord]) : StatsModel = 
    StatsModel(
      seasonId,
      game.gameId,
      game.dateCompleted.isDefined,
      demos.map(GameDemoModel.toModel(_)),
      RoundModel.toModel(roundStats)
    )
}

case class GameDemoModel(playerId: Int, playerName: String, demoData: Option[DemoData])
object GameDemoModel {
  def toModel(demo: DemoStatusRecord) = GameDemoModel(
    demo.playerId,
    demo.playerName,
    demo.demoDetails.map(DemoData.toModel(_))
  )

  def toModel(playerName: String, demo: GameDemo) = GameDemoModel(
    demo.playerId,
    playerName,
    Some(DemoData(
      demo.gameDemoId,
      demo.filename,
      demo.dateUploaded
    ))
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

case class RoundModel(
  roundId: Int,
  mapNumber: String,
  playerData: Seq[RoundStatsModel]
)

object RoundModel {
  def toModel(data: Seq[RoundStatsRecord]) : Seq[RoundModel] = 
    data.groupBy(_.roundId).map { case(roundId, data) =>
      RoundModel(roundId, data.head.mapNumber, data.map(RoundStatsModel.toModel(_)))
    }.toSeq.sortBy(_.roundId)
}

case class RoundStatsModel(
  roundResultId: Int, 
  playerId: Int, 
  playerName: String,
  teamId: Int,
  teamName: String, 
  captures: Int, 
  pCaptures: Int, 
  drops: Int, 
  frags: Int, 
  deaths: Int
)

object RoundStatsModel {
  def toModel(data: RoundStatsRecord) = 
    RoundStatsModel(
      data.roundResultId, 
      data.playerId, 
      data.playerName, 
      data.teamId,
      data.teamName,
      data.captures, 
      data.pCaptures, 
      data.drops, 
      data.frags, 
      data.deaths
    )

  def toModel(roundId: Int, data: RoundResult) = 
    RoundStatsModel(
      data.roundResultId,
      data.playerId
    )

  def toEntity(roundId: Int, model: RoundStatsModel) = 
    RoundResult(
      model.roundResultId,
      roundId,
      model.playerId,
      model.captures,
      model.pCaptures,
      model.drops,
      model.frags,
      model.deaths
    )
}

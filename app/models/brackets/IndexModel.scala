package models.brackets

import play.api.libs.json.Json
import data._

case class IndexModel(playoffStats: Seq[PlayoffStatsModel], regularStats: Seq[RegularSeasonStatsModel])
case class PlayoffStatsModel(teamId: Int, teamName: String, gameId: Int, weekId: Int, captures: Int)
case class RegularSeasonStatsModel(teamId: Int, teamName: String, gameId: Int, wins: Int, losses: Int, ties: Int)

object IndexModel {
  implicit val writesRegularSeasonStatsModel = Json.writes[RegularSeasonStatsModel]
  implicit val writesPlayoffStatsModel = Json.writes[PlayoffStatsModel]
  implicit val writesIndexModel = Json.writes[IndexModel]

  def toModel(playoffStats: Seq[TeamGameResultRecord]) = IndexModel(
    playoffStats.map(PlayoffStatsModel.toModel(_)),
    Seq.empty[RegularSeasonStatsModel]
  )
}

object PlayoffStatsModel {
  def toModel(data: TeamGameResultRecord) = 
    PlayoffStatsModel(data.teamId, data.teamName, data.gameId, data.weekId, data.captures)
}

object RegularSeasonStatsModel {
  def toModel() = ???
}

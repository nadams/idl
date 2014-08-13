package models.brackets

import play.api.libs.json.Json
import data._

case class IndexModel(playoffStats: Seq[PlayoffStatsModel])
case class PlayoffStatsModel(teamId: Int, teamName: String, gameId: Int, weekId: Int, captures: Int)

object IndexModel {
  implicit val writesPlayoffStatsModel = Json.writes[PlayoffStatsModel]
  implicit val writesIndexModel = Json.writes[IndexModel]

  def toModel(playoffStats: Seq[TeamGameResultRecord]) = IndexModel(playoffStats.map(PlayoffStatsModel.toModel(_)))
}

object PlayoffStatsModel {
  def toModel(data: TeamGameResultRecord) = 
    PlayoffStatsModel(data.teamId, data.teamName, data.gameId, data.weekId, data.captures)
}

package models.brackets

import play.api.libs.json.Json
import data._

case class IndexModel(stats: Seq[TeamStatsModel])
case class TeamStatsModel(teamId: Int, teamName: String, gameId: Int, weekId: Int, captures: Int)

object IndexModel {
  implicit val writesTeamStatsModel = Json.writes[TeamStatsModel]
  implicit val writesIndexModel = Json.writes[IndexModel]

  def toModel(stats: Seq[TeamGameResultRecord]) = IndexModel(stats.map(TeamStatsModel.toModel(_)))
}

object TeamStatsModel {
  def toModel(data: TeamGameResultRecord) = 
    TeamStatsModel(data.teamId, data.teamName, data.gameId, data.weekId, data.captures)
}

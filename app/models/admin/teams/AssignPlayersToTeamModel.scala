package models.admin.teams

import play.api.libs.json.Json

case class AssignPlayersToTeamModel(teamId: Int, playerIds: Seq[Int])

object AssignPlayersToTeamModel {
  implicit val reads = Json.reads[AssignPlayersToTeamModel]
}

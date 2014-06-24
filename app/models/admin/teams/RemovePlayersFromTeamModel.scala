package models.admin.teams

import play.api.libs.json.Json

case class RemovePlayersFromTeamModel(teamId: Int, playerIds: Seq[Int])

object RemovePlayersFromTeamModel {
  implicit val reads = Json.reads[RemovePlayersFromTeamModel]
}
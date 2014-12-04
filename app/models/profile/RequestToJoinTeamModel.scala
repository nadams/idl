package models.profile

import play.api.libs.json.Json

case class RequestToJoinTeamModel(playerId: Int, teamName: String)

object RequestToJoinTeamModel {
  implicit val readsModel = Json.reads[RequestToJoinTeamModel]
}

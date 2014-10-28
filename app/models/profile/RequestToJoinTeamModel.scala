package models.profile

import play.api.libs.json.Json

case class RequestToJoinTeamModel(playerId: Int, teamId: Int)

object RequestToJoinTeamModel {
  implicit val readsModel = Json.reads[RequestToJoinTeamModel]
}

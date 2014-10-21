package models.profile

import play.api.libs.json.Json
import data._

case class AddPlayerNameResultModel(playerId: Int, playerName: String, isApproved: Boolean)

object AddPlayerNameResultModel {
  implicit val writesAddPlayerNameResultModel = Json.writes[AddPlayerNameResultModel] 

  def toModel(player: PlayerProfileRecord) = 
    AddPlayerNameResultModel(player.playerId, player.playerName, player.isApproved)
}

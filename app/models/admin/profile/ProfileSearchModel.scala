package models.admin.profile

import play.api.libs.json.Json
import data.ProfileSearchRecord

case class ProfileSearchModel(results: Seq[ProfileSearchResultModel])

object ProfileSearchModel {
  implicit val writesPlayerNameSearchResultModel = Json.writes[PlayerNameSearchResultModel]
  implicit val writesProfileSearchResultModel = Json.writes[ProfileSearchResultModel]
  implicit val writesProfileSearchModel = Json.writes[ProfileSearchModel]

  def toModel(data: Seq[ProfileSearchRecord]) = 
    data.groupBy(x => (x.profileId, x.displayName, x.email)).map { case (key, value) =>
      ProfileSearchResultModel(key._1, key._2, key._3, value.map(PlayerNameSearchResultModel.toModel(_)))
    }
}

case class ProfileSearchResultModel(profileId: Int, displayName: String, email: String, playerNames: Seq[PlayerNameSearchResultModel])

case class PlayerNameSearchResultModel(playerId: Int, playerName: String, isApproved: Boolean)

object PlayerNameSearchResultModel {
  def toModel(data: ProfileSearchRecord) = 
    PlayerNameSearchResultModel(data.playerId, data.playerName, data.isApproved)
}

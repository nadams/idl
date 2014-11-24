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
      ProfileSearchResultModel(key._1, key._2, key._3, value.flatMap { x => for {
        playerId <- x.playerId
        playerName <- x.playerName
        isApproved <- x.isApproved
      } yield PlayerNameSearchResultModel(playerId, playerName, isApproved) } ) 
    }.toSeq.sortBy(_.displayName)
}

case class ProfileSearchResultModel(profileId: Int, displayName: String, email: String, playerNames: Seq[PlayerNameSearchResultModel])

case class PlayerNameSearchResultModel(playerId: Int, playerName: String, isApproved: Boolean)

object PlayerNameSearchResultModel {
  def toModel(playerId: Int, playerName: String, isApproved: Boolean) = 
    PlayerNameSearchResultModel(playerId, playerName, isApproved)
}

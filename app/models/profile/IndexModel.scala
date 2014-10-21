package models.profile

import play.api.libs.json.Json
import data._

case class IndexModel(profileId: Int, profileIsPlayer: Boolean, profileModel: ProfileModel, playerModel: PlayerModel)

object IndexModel {
  implicit val writesPlayerName = Json.writes[PlayerName]
  implicit val writesPlayerModel = Json.writes[PlayerModel]
  implicit val writesProfileModel = Json.writes[ProfileModel]
  implicit val writesIndexModel = Json.writes[IndexModel] 

  def toModel(profileId: Int, profileIsPlayer: Boolean, players: Seq[PlayerProfileRecord]) = 
    IndexModel(profileId, profileIsPlayer, ProfileModel.toModel, PlayerModel.toModel(players))
}

case class ProfileModel(displayName: String)

object ProfileModel {
  def toModel() = ProfileModel("arst")
}

case class PlayerModel(playerNames: Seq[PlayerName])

object PlayerModel {
  def toModel(players: Seq[PlayerProfileRecord]) = 
    PlayerModel(players.map(PlayerName.toModel(_)))
}

case class PlayerName(playerId: Int, playerName: String, isApproved: Boolean)

object PlayerName {
  def toModel(player: PlayerProfileRecord) = 
    PlayerName(player.playerId, player.playerName, player.isApproved)  
}


package models.profile

import play.api.libs.json.Json
import data._

case class IndexModel(profileId: Int, profileIsPlayer: Boolean, playerNames: Seq[PlayerName])

object IndexModel {
  implicit val writesPlayerName = Json.writes[PlayerName]
  implicit val writesIndexModel = Json.writes[IndexModel] 

  def toModel(profileId: Int, profileIsPlayer: Boolean, players: Seq[Player]) = 
    IndexModel(profileId, profileIsPlayer, players.map(PlayerName.toModel(_)))
}

case class PlayerName(playerId: Int, playerName: String, isApproved: Boolean)

object PlayerName {
  def toModel(player: Player) = PlayerName(player.playerId, player.playerName, true)  
}


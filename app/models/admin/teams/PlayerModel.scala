package models.admin.teams

import play.api.libs.json.Json
import data.Player

case class PlayersModel(players: Seq[PlayerModel])
case class PlayerModel(playerId: Int, playerName: String, teamId: Option[Int])

object PlayersModel {
  implicit val writesPlayerModel = Json.writes[PlayerModel]
  implicit val writesPlayersModel = Json.writes[PlayersModel]

  def toModel(players: Seq[Player]) = PlayersModel(players.map { player =>
    PlayerModel(player.playerId, player.name, player.teamId)
  })
}

package models.admin.teams

import play.api.libs.json.Json
import data.TeamPlayerRecord

case class PlayersModel(players: Seq[PlayerModel])
case class PlayerModel(playerId: Int, playerName: String, teamIds: Seq[TeamPlayerModel])
case class TeamPlayerModel(teamId: Int, isCaptain: Boolean)

object PlayersModel {
  implicit val writesTeamPlayerModel = Json.writes[TeamPlayerModel]
  implicit val writesPlayerModel = Json.writes[PlayerModel]
  implicit val writesPlayersModel = Json.writes[PlayersModel]

  def toModel(players: Seq[TeamPlayerRecord]) = PlayersModel(
    players.groupBy(_.playerId).map { case (key, value) =>
      value.headOption.map { x => 
        PlayerModel(x.playerId, x.playerName, value.flatMap { 
          case TeamPlayerRecord(_, _, Some(teamId), Some(isCaptain)) => Some(TeamPlayerModel(teamId, isCaptain))
          case _ => None
        })
      }.getOrElse(PlayerModel(0, "", Seq.empty[TeamPlayerModel]))
    }.toSeq
  )
}

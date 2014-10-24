package models.profile

import play.api.libs.json.Json
import data._

case class IndexModel(profileId: Int, profileIsPlayer: Boolean, profileModel: ProfileModel, playerModel: PlayerModel, teams: Seq[TeamMembershipModel])

object IndexModel {
  implicit val writesTeamMembershipPlayerModel = Json.writes[TeamMembershipPlayerModel]
  implicit val writesTeamMembershipModel = Json.writes[TeamMembershipModel]
  implicit val writesPlayerName = Json.writes[PlayerName]
  implicit val writesPlayerModel = Json.writes[PlayerModel]
  implicit val writesProfileModel = Json.writes[ProfileModel]
  implicit val writesIndexModel = Json.writes[IndexModel] 

  def toModel(profileId: Int, profileIsPlayer: Boolean, profile: Profile, players: Seq[PlayerProfileRecord], teams: Seq[FellowPlayerRecord]) = 
    IndexModel(profileId, profileIsPlayer, ProfileModel.toModel(profile), PlayerModel.toModel(players), TeamMembershipModel.toModel(teams))
}

case class ProfileModel(displayName: String)

object ProfileModel {
  def toModel(profile: Profile) = ProfileModel(profile.displayName)
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

case class TeamMembershipModel(teamId: Int, teamName: String, members: Seq[TeamMembershipPlayerModel])

object TeamMembershipModel {
  def toModel(teams: Seq[FellowPlayerRecord]) = 
    teams.groupBy(x => (x.teamId, x.teamName)).map { case (teamInfo, value) =>
      TeamMembershipModel(teamInfo._1, teamInfo._2, value.map(y => TeamMembershipPlayerModel(y.playerId, y.playerName))) 
    }.toSeq
}

case class TeamMembershipPlayerModel(playerId: Int, playerName: String)

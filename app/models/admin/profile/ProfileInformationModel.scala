package models.admin.profile

import play.api.libs.json.Json
import org.joda.time.DateTime
import data._
import formatters.DateTimeFormatter._
import security.Roles

case class ProfileInformationModel(
  profileId: Int, 
  displayName: String, 
  email: String, 
  passwordExpired: Boolean, 
  lastLoginDate: DateTime, 
  profileRoles: Seq[ProfileRoleModel],
  playerNames: Seq[PlayerNameModel],
  allRoles: Seq[ProfileRoleModel]
)

object ProfileInformationModel {
  implicit val writesPlayerNameModel = Json.writes[PlayerNameModel]
  implicit val writesProfileRoleModel = Json.writes[ProfileRoleModel]
  implicit val writesProfileInformationModel = Json.writes[ProfileInformationModel]

  def toModel(profile: Profile, profileRoles: Seq[Roles.Role], playerNames: Seq[Player]) = ProfileInformationModel(
    profile.profileId,
    profile.displayName,
    profile.email,
    profile.passwordExpired,
    profile.lastLoginDate,
    profileRoles.map(ProfileRoleModel.toModel(_)),
    playerNames.map(PlayerNameModel.toModel(_)),
    Roles.values.map(ProfileRoleModel.toModel(_)).toSeq
  )
}

case class PlayerNameModel(playerId: Int, playerName: String, isApproved: Boolean)

object PlayerNameModel {
  def toModel(player: Player) = PlayerNameModel(
    player.playerId,
    player.playerName,
    true
  )
}

case class ProfileRoleModel(roleId: Int, roleName: String)

object ProfileRoleModel {
  def toModel(data: Roles.Role) = ProfileRoleModel(data.id, data.toString)
}


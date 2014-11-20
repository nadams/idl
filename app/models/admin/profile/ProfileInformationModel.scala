package models.admin.profile

import org.joda.time.DateTime
import data._

case class ProfileInformationModel(
  profileId: Int, 
  displayName: String, 
  email: String, 
  passwordExpired: Boolean, 
  lastLoginDate: DateTime, 
  playerNames: Seq[PlayerNameModel]
)

object ProfileInformationModel {
  def toModel(profile: Profile, playerNames: Seq[Player]) = ProfileInformationModel(
    profile.profileId,
    profile.displayName,
    profile.email,
    profile.passwordExpired,
    profile.lastLoginDate,
    playerNames.map(PlayerNameModel.toModel(_))
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


package services

import scala.util.Try
import org.joda.time.{ DateTime, DateTimeZone }
import data._

trait PlayerServiceComponent {
  val playerService: PlayerService

  trait PlayerService {
    def profileIsPlayer(profileId: Int) : Boolean
    def makeProfileAPlayer(profile: Profile) : Option[PlayerProfileRecord] 
    def getPlayerNamesThatExist(names: Set[String]) : Set[String]
    def getPlayer(playerId: Int) : Option[Player]
    def getPlayerByName(name: String) : Option[Player]
    def createPlayerFromName(name: String) : Player
    def batchCreatePlayerFromName(names: Set[String]) : Set[Player]
    def getPlayers() : Seq[TeamPlayerRecord]
    def removePlayerFromProfile(profileId: Int, playerId: Int) : Boolean
    def createOrAddPlayerToProfile(profileId: Int, playerName: String) : Try[Option[Player]]
    def getPlayerProfile(profileId: Int, playerId: Int) : Option[PlayerProfile]
    def getPlayerProfileRecord(profileId: Int, playerId: Int) : Option[PlayerProfileRecord]
    def getPlayerProfileRecordsForProfile(profileId: Int) : Seq[PlayerProfileRecord] 
    def getFellowPlayersForProfile(profileId: Int) : Seq[FellowPlayerRecord]
    def getFellowPlayersForTeamPlayer(profileId: Int, playerId: Int, teamId: Int) : Seq[FellowPlayerRecord]
    def getPlayersByProfileId(profileId: Int) : Seq[Player]
    def unapprovePlayer(profileId: Int, playerId: Int) : Try[Int]
    def approvePlayer(profileId: Int, playerId: Int) : Try[Int]
    def removePlayer(profileId: Int, playerId: Int) : Try[Int]
  }
}

trait PlayerServiceComponentImpl extends PlayerServiceComponent {
  self: PlayerRepositoryComponent => 
  val playerService = new PlayerServiceImpl

  class PlayerServiceImpl extends PlayerService {
    def getPlayer(playerId: Int) = playerRepository.getPlayer(playerId)
    def getPlayerByName(name: String) = playerRepository.getPlayerByName(name)
    def getPlayerNamesThatExist(names: Set[String]) = playerRepository.getPlayerNamesThatExist(names)
    def createPlayerFromName(name: String) = playerRepository.createPlayerFromName(name)
    def getPlayers() = playerRepository.getPlayers() 
    def removePlayerFromProfile(profileId: Int, playerId: Int) = playerRepository.removePlayerFromProfile(profileId, playerId)
    def getPlayerProfile(profileId: Int, playerId: Int) = playerRepository.getPlayerProfile(profileId, playerId)
    def getPlayerProfileRecord(profileId: Int, playerId: Int) = playerRepository.getPlayerProfileRecord(profileId, playerId)
    def getPlayerProfileRecordsForProfile(profileId: Int) = playerRepository.getPlayerProfileRecordsForProfile(profileId)
    def getFellowPlayersForProfile(profileId: Int) = playerRepository.getFellowPlayersForProfile(profileId)
    def getFellowPlayersForTeamPlayer(profileId: Int, playerId: Int, teamId: Int) = playerRepository.getFellowPlayersForTeamPlayer(profileId, playerId, teamId)
    def getPlayersByProfileId(profileId: Int) = playerRepository.getPlayersByProfileId(profileId)

    def createOrAddPlayerToProfile(profileId: Int, playerName: String) = Try(
      if(playerRepository.getNumberOfPlayersForProfile(profileId) <= 15) {
        playerRepository.getPlayerByName(playerName) map { player => 
          if(playerRepository.playerIsInAnyProfile(player.playerId)) None 
          else playerRepository.addPlayerProfile(profileId, player.playerId, true) 
        } getOrElse(playerRepository.createPlayerAndAssignToProfile(profileId, playerName))
      } else {
        throw ProfileExceededPlayerCount() 
      }
    )
    
    def profileIsPlayer(profileId: Int) = 
      playerRepository.getPlayersByProfileId(profileId).nonEmpty

    def makeProfileAPlayer(profile: Profile) = 
      getPlayerByName(profile.displayName) map { existingPlayer => 
        if(playerRepository.getPlayerProfileByPlayerId(existingPlayer.playerId).isEmpty) {
          if(playerRepository.insertPlayerProfile(existingPlayer.playerId, profile.profileId, false)) {
            getPlayerProfileRecord(profile.profileId, existingPlayer.playerId)
          } else {
            None
          }
        } else {
          None
        }
      } getOrElse {
        playerRepository.insertPlayerWithProfile(
          Player(0, profile.displayName, true, new DateTime(DateTimeZone.UTC), true),
          profile.profileId,
          true
        ).map(playerRepository.getPlayerProfileRecord(profile.profileId, _)) getOrElse(None)
      }

    def batchCreatePlayerFromName(names: Set[String]) = 
      playerRepository.batchCreatePlayerFromName(names.diff(playerService.getPlayerNamesThatExist(names)))

    def unapprovePlayer(profileId: Int, playerId: Int) = Try(
      playerRepository.getPlayerProfile(profileId, playerId) map { playerProfile =>
        if(playerRepository.updatePlayerProfile(playerProfile.copy(isApproved = false))) playerProfile.playerId
        else throw CouldNotUpdatePlayerProfileException("Unable to unapprove player profile")
      } getOrElse(throw CouldNotGetPlayerProfile())
    )

    def approvePlayer(profileId: Int, playerId: Int) = Try(
      playerRepository.getPlayerProfile(profileId, playerId) map { playerProfile =>
        if(playerRepository.updatePlayerProfile(playerProfile.copy(isApproved = true))) playerProfile.playerId
        else throw CouldNotUpdatePlayerProfileException("Unable to approve player profile")
      } getOrElse(throw CouldNotGetPlayerProfile())
    )

    def removePlayer(profileId: Int, playerId: Int) = Try(
      if(playerRepository.removePlayerFromProfile(profileId, playerId)) playerId
      else throw CouldNotUpdatePlayerProfileException("Could not remove player profile from player") 
    )
  }
}

case class ProfileExceededPlayerCount() extends Exception()

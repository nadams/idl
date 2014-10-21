package services

import data._

trait PlayerServiceComponent {
  val playerService: PlayerService

  trait PlayerService {
    def profileIsPlayer(profileId: Int) : Boolean
    def makeProfileAPlayer(profile: Profile) : Boolean
    def getPlayerNamesThatExist(names: Set[String]) : Set[String]
    def getPlayer(playerId: Int) : Option[Player]
    def getPlayerByName(name: String) : Option[Player]
    def createPlayerFromName(name: String) : Player
    def batchCreatePlayerFromName(names: Set[String]) : Set[Player]
    def getPlayers() : Seq[TeamPlayerRecord]
    def getPlayersForProfile(profileId: Int) : Seq[Player]
    def removePlayerFromProfile(profileId: Int, playerId: Int) : Boolean
    def createOrAddPlayerToProfile(profileId: Int, playerName: String) : Option[Player]
    def getPlayerProfile(profileId: Int, playerId: Int) : Option[PlayerProfile]
    def getPlayerProfileRecord(profileId: Int, playerId: Int) : Option[PlayerProfileRecord]
    def getPlayerProfileRecordsForProfile(profileId: Int) : Seq[PlayerProfileRecord] 
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
    def getPlayersForProfile(profileId: Int) = playerRepository.getPlayersForProfile(profileId)
    def removePlayerFromProfile(profileId: Int, playerId: Int) = playerRepository.removePlayerFromProfile(profileId, playerId)
    def getPlayerProfile(profileId: Int, playerId: Int) = playerRepository.getPlayerProfile(profileId, playerId)
    def getPlayerProfileRecord(profileId: Int, playerId: Int) = playerRepository.getPlayerProfileRecord(profileId, playerId)
    def getPlayerProfileRecordsForProfile(profileId: Int) = playerRepository.getPlayerProfileRecordsForProfile(profileId)

    def createOrAddPlayerToProfile(profileId: Int, playerName: String) = 
      playerRepository.getPlayerByName(playerName) map { player => 
        if(playerRepository.playerIsInAnyProfile(player.playerId)) playerRepository.addPlayerProfile(profileId, player.playerId, true)
        else playerRepository.addPlayerProfile(profileId, player.playerId, true) 
      } getOrElse(playerRepository.createPlayerAndAssignToProfile(profileId, playerName))
    
    def profileIsPlayer(profileId: Int) = 
      playerRepository.getPlayersByProfileId(profileId).nonEmpty

    def makeProfileAPlayer(profile: Profile) = 
      playerRepository.insertPlayerWithProfile(Player(0, profile.displayName, true, None), profile.profileId)

    def batchCreatePlayerFromName(names: Set[String]) = 
      playerRepository.batchCreatePlayerFromName(names.diff(playerService.getPlayerNamesThatExist(names)))
  }
}

package services

import data._

trait PlayerServiceComponent {
  val playerService: PlayerService

  trait PlayerService {
    def profileIsPlayer(profileId: Int) : Boolean
    def makeProfileAPlayer(profile: Profile) : Boolean
  }
}

trait PlayerServiceComponentImpl extends PlayerServiceComponent {
  self: PlayerRepositoryComponent => 
  val playerService = new PlayerServiceImpl

  class PlayerServiceImpl extends PlayerService {
    def profileIsPlayer(profileId: Int) = 
      playerRepository.getPlayerByProfileId(profileId).isDefined

    def makeProfileAPlayer(profile: Profile) =
      playerRepository.insertPlayerWithProfile(Player(0, profile.displayName, true, None), profile.profileId)
  }
}

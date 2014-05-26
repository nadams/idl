package services

import data._

trait TeamServiceComponent {
  val teamService: TeamService

  trait TeamService {
    def getTeamsForSeason(seasonId: Int) : Seq[Team]
    def getAllPlayers() : Seq[Player]
  }
}

trait TeamServiceComponentImpl extends TeamServiceComponent {
  self: TeamRepositoryComponent with PlayerRepositoryComponent =>
  val teamService = new TeamServiceImpl

  class TeamServiceImpl extends TeamService {
    def getTeamsForSeason(seasonId: Int) = teamRepository.getTeamsForSeason(seasonId)
    def getAllPlayers() = playerRepository.getAllPlayers
  }
}

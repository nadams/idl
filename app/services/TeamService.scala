package services

import data._

trait TeamServiceComponent {
  val teamService: TeamService

  trait TeamService {
    def getTeamsForSeason(seasonId: Int) : Seq[Team]
  }
}

trait TeamServiceComponentImpl extends TeamServiceComponent {
  self: TeamRepositoryComponent =>
  val teamService = new TeamServiceImpl

  class TeamServiceImpl extends TeamService {
    def getTeamsForSeason(seasonId: Int) = teamRepository.getTeamsForSeason(seasonId)
  }
}

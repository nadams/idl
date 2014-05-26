package services

import data._

trait TeamServiceComponent {
  val teamService: TeamService

  trait TeamService {
    def getTeamsForSeason(seasonId: Int) : Seq[Team]
    def getAllPlayers() : Seq[Player]
    def assignPlayersToTeam(teamId: Int, seasonId: Int, playerIds: Seq[Int]) : Seq[Int]
  }
}

trait TeamServiceComponentImpl extends TeamServiceComponent {
  self: TeamRepositoryComponent with PlayerRepositoryComponent =>
  val teamService = new TeamServiceImpl

  class TeamServiceImpl extends TeamService {
    def getTeamsForSeason(seasonId: Int) = teamRepository.getTeamsForSeason(seasonId)
    def getAllPlayers() = playerRepository.getAllPlayers
    def assignPlayersToTeam(teamId: Int, seasonId: Int, playerIds: Seq[Int]) =
      playerIds.filter { playerId => teamRepository.assignPlayerToTeam(playerId, teamId, seasonId) }
  }
}

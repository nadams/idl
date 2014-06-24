package services

import data._

trait TeamServiceComponent {
  val teamService: TeamService

  trait TeamService {
    def getTeamsForSeason(seasonId: Int) : Seq[Team]
    def getAllPlayers() : Seq[Player]
    def assignPlayersToTeam(teamId: Int, playerIds: Seq[Int]) : Seq[Int]
    def updateTeamPlayer(playerId: Int, teamId: Int, isCaptain: Boolean) : Boolean
    def getAllEligibleTeams() : Seq[Team]
    def insertTeam(team: Team) : Boolean
    def updateTeam(team: Team) : Boolean
    def getTeam(id: Int) : Option[Team]
    def getAllTeams() : Seq[Team]
  }
}

trait TeamServiceComponentImpl extends TeamServiceComponent {
  self: TeamRepositoryComponent with PlayerRepositoryComponent =>
  val teamService = new TeamServiceImpl

  class TeamServiceImpl extends TeamService {
    def getTeamsForSeason(seasonId: Int) = teamRepository.getTeamsForSeason(seasonId)
    def getAllPlayers() = playerRepository.getAllPlayers
    def getAllEligibleTeams() = teamRepository.getAllActiveTeams
    def insertTeam(team: Team) = teamRepository.insertTeam(team)
    def updateTeam(team: Team) = teamRepository.updateTeam(team)
    def getTeam(teamId: Int) = teamRepository.getTeam(teamId)
    def getAllTeams() = teamRepository.getAllTeams

    def assignPlayersToTeam(teamId: Int, playerIds: Seq[Int]) =
      playerIds.filter { playerId => teamRepository.assignPlayerToTeam(playerId, teamId) }

    def removePlayersFromTeam(teamId: Int, playerIds: Seq[Int]) =
      playerIds.filter { playerId => teamRepository.removePlayerFromTeam(playerId, teamId) }
      
    def updateTeamPlayer(playerId: Int, teamId: Int, isCaptain: Boolean) = 
      teamRepository.updateTeamPlayer(playerId, teamId, isCaptain)
  }
}

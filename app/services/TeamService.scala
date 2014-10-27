package services

import data._

trait TeamServiceComponent {
  val teamService: TeamService

  trait TeamService {
    def getTeamsForSeason(seasonId: Int) : Seq[Team]
    def getAllPlayers() : Seq[Player]
    def assignPlayersToTeam(teamId: Int, playerIds: Seq[Int]) : Seq[Int]
    def getAllEligibleTeams() : Seq[Team]
    def insertTeam(team: Team) : Boolean
    def updateTeam(team: Team) : Boolean
    def getTeam(id: Int) : Option[Team]
    def getAllTeams() : Seq[Team]
    def removeTeam(teamId: Int) : Boolean
    def getTeamsForGame(gameId: Int) : Option[(Team, Team)]
    def makeCaptain(teamId: Int, playerId: Int) : Option[Int]
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
    def removeTeam(teamId: Int) = teamRepository.removeTeam(teamId)
    def getTeamsForGame(gameId: Int) = teamRepository.getTeamsForGame(gameId)
    def makeCaptain(teamId: Int, playerId: Int) = teamRepository.makeCaptain(teamId, playerId)

    def assignPlayersToTeam(teamId: Int, playerIds: Seq[Int]) =
      playerIds.filter { playerId => teamRepository.assignPlayerToTeam(playerId, teamId, isApproved = true) }

    def removePlayersFromTeam(teamId: Int, playerIds: Seq[Int]) =
      playerIds.filter { playerId => teamRepository.removePlayerFromTeam(playerId, teamId) }
  }
}

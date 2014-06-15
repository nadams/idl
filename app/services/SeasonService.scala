package services

import org.joda.time.DateTime
import data._

trait SeasonServiceComponent {
  val seasonService: SeasonService

  trait SeasonService {
    def getAllSeasons(): Seq[Season]
    def getSeasonById(id: Int) : Option[Season]
    def insertSeason(season: Season): Boolean
    def updateSeason(seasonId: Int, name: String, startDate: DateTime, endDate: DateTime) : Boolean
    def removeSeason(seasonId: Int) : Boolean
    def removeTeamsFromSeason(seasonId: Int, teamIds: Seq[Int]) : Seq[Int]
    def assignTeamsToSeason(seasonId: Int, teamIds: Seq[Int]) : Seq[Int]
  }
}

trait SeasonServiceComponentImpl extends SeasonServiceComponent {
  self: SeasonRepositoryComponent =>
  val seasonService: SeasonService = new SeasonServiceImpl

  class SeasonServiceImpl extends SeasonService {
    def getAllSeasons() = seasonRepository.getAllSeasons
    def insertSeason(season: Season) = seasonRepository.insertSeason(season)
    def getSeasonById(id: Int) = seasonRepository.getSeasonById(id)
    def removeSeason(id: Int) = seasonRepository.removeSeason(id)
    def updateSeason(seasonId: Int, name: String, startDate: DateTime, endDate: DateTime) = 
      getSeasonById(seasonId).exists(season => seasonRepository.updateSeason(season.update(name, startDate, endDate)))

    def removeTeamsFromSeason(seasonId: Int, teamIds: Seq[Int]) = 
      teamIds.filter { teamId => seasonRepository.removeTeamFromSeason(seasonId, teamId) }

    def assignTeamsToSeason(seasonId: Int, teamIds: Seq[Int]) =
      teamIds.filter { teamId => seasonRepository.assignTeamToSeason(seasonId, teamId) }
  }
}

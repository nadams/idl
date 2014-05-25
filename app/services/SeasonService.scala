package services

import data._

trait SeasonServiceComponent {
  val seasonService: SeasonService

  trait SeasonService {
    def getAllSeasons(): Seq[Season]
    def getSeasonById(id: Int) : Option[Season]
    def insertSeason(season: Season): Boolean
  }
}

trait SeasonServiceComponentImpl extends SeasonServiceComponent {
  self: SeasonRepositoryComponent =>
  val seasonService: SeasonService = new SeasonServiceImpl

  class SeasonServiceImpl extends SeasonService {
    def getAllSeasons() = seasonRepository.getAllSeasons
    def insertSeason(season: Season) = seasonRepository.insertSeason(season)
    def getSeasonById(id: Int) = seasonRepository.getSeasonById(id)
  }
}

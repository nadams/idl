package services

import data._

trait SeasonServiceComponent {
  val seasonService: SeasonService

  trait SeasonService {
    def getAllSeasons: Seq[Season]
  }
}

trait SeasonServiceComponentImpl extends SeasonServiceComponent {
  self: SeasonRepositoryComponent =>
  val seasonService: SeasonService = new SeasonServiceImpl

  class SeasonServiceImpl extends SeasonService {
    def getAllSeasons = seasonRepository.getAllSeasons
  }
}

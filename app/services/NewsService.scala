package services

import data._

trait NewsServiceComponent {
  val newsService: NewsService

  trait NewsService {
    def getAllNews() : Seq[News]
    def getPagedNews(currentPage: Int = 1, pageSize: Int = 15) : Seq[News]
  }
}

trait NewsServiceComponentImpl extends NewsServiceComponent {
  self: NewsRepositoryComponent =>
  override val newsService: NewsService = new NewsServiceImpl

  class NewsServiceImpl extends NewsService {
    override def getAllNews() = newsRepository.getAllNews
    override def getPagedNews(currentPage: Int = 1, pageSize: Int = 15) = newsRepository.getPagedNews(currentPage, pageSize)
  }
}

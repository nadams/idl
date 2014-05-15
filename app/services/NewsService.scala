package services

import data._

trait NewsServiceComponent {
  val newsService: NewsService

  trait NewsService {
    def getAllNews() : Seq[News]
    def getPagedNews(currentPage: Int = 1, pageSize: Int = 15) : Seq[News]
    def removeNewsItem(id: Int) : Boolean
    def getNewsById(id: Int) : Option[News]
    def insertNews(news: News) : Int
  }
}

trait NewsServiceComponentImpl extends NewsServiceComponent {
  self: NewsRepositoryComponent =>
  override val newsService: NewsService = new NewsServiceImpl

  class NewsServiceImpl extends NewsService {
    def getAllNews() = newsRepository.getAllNews
    def getPagedNews(currentPage: Int = 1, pageSize: Int = 15) = newsRepository.getPagedNews(currentPage, pageSize)
    def removeNewsItem(id: Int) = newsRepository.removeNewsItem(id)
    def getNewsById(id: Int) : Option[News] = newsRepository.getNewsById(id)
    def insertNews(news: News) : Int = newsRepository.insertNews(news)
  }
}

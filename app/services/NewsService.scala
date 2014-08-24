package services

import data._
import org.joda.time.{ DateTime, DateTimeZone }

trait NewsServiceComponent {
  val newsService: NewsService

  trait NewsService {
    def getAllNews() : Seq[News]
    def getPagedNews(currentPage: Int = 1, pageSize: Int = 15) : Seq[News]
    def removeNewsItem(id: Int) : Boolean
    def getNewsById(id: Int) : Option[News]
    def insertNews(news: News) : Boolean
    def updateNews(newsId: Int, subject: String, content: String) : Boolean
  }
}

trait NewsServiceComponentImpl extends NewsServiceComponent {
  self: NewsRepositoryComponent =>
  override val newsService: NewsService = new NewsServiceImpl

  class NewsServiceImpl extends NewsService {
    def getAllNews() = newsRepository.getAllNews
    def getPagedNews(currentPage: Int = 1, pageSize: Int = 15) = newsRepository.getPagedNews(currentPage, pageSize)
    def removeNewsItem(id: Int) = newsRepository.removeNewsItem(id)
    def getNewsById(id: Int) = newsRepository.getNewsById(id)
    def insertNews(news: News) = newsRepository.insertNews(news)
    def updateNews(newsId: Int, subject: String, content: String) = 
      getNewsById(newsId).exists(news => newsRepository.updateNews(news.updateContent(subject, content))) 
  }
}

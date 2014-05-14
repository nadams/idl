package models.news

import data.News

case class EditNews(newsId: Int, subject: String, content: String) {
  val isNewNews = newsId == 0
}

object EditNews {
  def apply() : EditNews = EditNews(0, "", "")
  def apply(news: News) : EditNews = EditNews(news.newsId, news.subject, news.content)
}

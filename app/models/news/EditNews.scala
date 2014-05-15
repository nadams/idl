package models.news

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import _root_.data.News
import components.{ NewsComponentImpl, ProfileComponentImpl }

case class EditNews(newsId: Int, subject: String, content: String) {
  val isNewNews = newsId == 0
}

object EditNews {
  def apply() : EditNews = EditNews(0, "", "")
  def apply(news: News) : EditNews = EditNews(news.newsId, news.subject, news.content)
}

object EditNewsForm {
  def apply() = 
    Form(
      mapping(
        "newsId" -> number,
        "subject" -> nonEmptyText,
        "content" -> nonEmptyText
      )(EditNews.apply)(EditNews.unapply)
    )
}

case class EditNewsErrors(subjectError: Option[String], contentError: Option[String])
object EditNewsErrors {
  def apply() : EditNewsErrors = EditNewsErrors(None, None)
}

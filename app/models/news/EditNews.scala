package models.news

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import _root_.data.News
import components.{ NewsComponentImpl, ProfileComponentImpl }

case class EditNews(newsId: Int, subject: String, content: String) {
  val isNewNews = newsId == 0
}

object EditNews {
  implicit val writesEditNews = Json.writes[EditNews]

  def toModel(news: News) = EditNews(news.newsId, news.subject, news.content)
  def empty = EditNews(0, "", "")
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
